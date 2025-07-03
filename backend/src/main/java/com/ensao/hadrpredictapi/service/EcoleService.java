package com.ensao.hadrpredictapi.service;

import com.ensao.hadrpredictapi.entity.Ecole;
import com.ensao.hadrpredictapi.repository.EcoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EcoleService {

    @Autowired
    private EcoleRepository schoolRepository;

    private GeocodingService geocodingService;

    public Ecole saveSchool(Ecole ecole) {
        Optional<Ecole> existing = schoolRepository.findByNomAndCommune(ecole.getNom(), ecole.getCommune());

        if (existing.isPresent()) {
            Ecole existingEcole = existing.get();
            if (existingEcole.getLatitude() == null || existingEcole.getLongitude() == null) {
                double[] coords = geocodingService.getCoordinates(existingEcole.getNom());
                existingEcole.setLatitude(coords[0]);
                existingEcole.setLongitude(coords[1]);
                return schoolRepository.save(existingEcole);
            }
            return existingEcole;
        } else {
            if (ecole.getLatitude() == null || ecole.getLongitude() == null) {
                double[] coords = geocodingService.getCoordinates(ecole.getNom());
                ecole.setLatitude(coords[0]);
                ecole.setLongitude(coords[1]);
            }
            return schoolRepository.save(ecole);
        }
    }

    public List<Ecole> getAllSchools() {
        return schoolRepository.findAll();
    }

    public void updateAllSchoolsWithMissingCoordinates() {
        List<Ecole> schools = schoolRepository.findAll();
        for (Ecole ecole : schools) {
            if (ecole.getLatitude() == null || ecole.getLongitude() == null) {
                double[] coords = geocodingService.getCoordinates(ecole.getNom());
                ecole.setLatitude(coords[0]);
                ecole.setLongitude(coords[1]);
                schoolRepository.save(ecole);
            }
        }
    }
}
