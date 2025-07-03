package com.ensao.hadrpredictapi.controller;

import com.ensao.hadrpredictapi.entity.Ecole;
import com.ensao.hadrpredictapi.entity.Eleve;
import com.ensao.hadrpredictapi.repository.EcoleRepository;
import com.ensao.hadrpredictapi.service.EcoleService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@AllArgsConstructor
@RequestMapping("/api/schools")
public class SchoolController {

    @Autowired
    private EcoleService schoolService;
    private final EcoleRepository ecoleRepository;
    @PostMapping
    public ResponseEntity<Ecole> createSchool(@RequestBody Ecole ecole) {
        Ecole saved = schoolService.saveSchool(ecole);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Ecole> getAll() {
        return schoolService.getAllSchools();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSchoolById(@PathVariable Long id) {
        Ecole ecole = ecoleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Ecole non trouvee"));
        String cycle="Inconnu";
        if(ecole.getEleves() != null && !ecole.getEleves().isEmpty()) {
            cycle = ecole.getEleves().get(0).getCycle();
        }
        List<Eleve> eleves =ecole.getEleves();

        long total =eleves.size();

        long pred1 =eleves.stream().filter(e -> e.getPrediction()==1).count();
        long pred0=total - pred1;

        long garcons = eleves.stream().filter(e->e.getGenre().equalsIgnoreCase("Garçon")).count();
        long filles = total-garcons;

        long garconsPred1 = eleves.stream()
                .filter(e->e.getGenre().equalsIgnoreCase("Garçon") && e.getPrediction()==1)
                .count();
        long fillesPred1 = eleves.stream()
                .filter(e->e.getGenre().equalsIgnoreCase("Fille") && e.getPrediction()==1)
                .count();

        double pourcentageGarcon = garcons == 0 ? 0 : (double) garconsPred1/garcons *100;
        double pourcentageFille = filles == 0 ? 0 : (double) fillesPred1/filles *100;

        Map<String, Object> map= new HashMap<>();


        map.put("SchoolName", ecole.getNom());
        map.put("cycle", cycle);
        map.put("totalEleves", total);

        // Données pour PieChart
        List<Map<String, Object>> pieData = List.of(
                Map.of("name", "Exerce ces études", "value", pred1),
                Map.of("name", "Abandons", "value", pred0)
        );
        map.put("pieData", pieData);

        // Données pour BarChart
        List<Map<String, Object>> barData = List.of(
                Map.of("genre", "Garçons", "pourcent", Math.round(pourcentageGarcon * 10) / 10.0),
                Map.of("genre", "Filles", "pourcent", Math.round(pourcentageFille * 10) / 10.0)
        );
        map.put("barData", barData);


        return ResponseEntity.ok(map);
    }

    @GetMapping("/provinces")
    public List<String> getAllProvinces(){
        System.out.println("Requête reçue : /api/schools/provinces");
        return ecoleRepository.findAllProvinces();
    }

    @GetMapping("/communes/{province}")
    public List<String> getAllCommunes(@PathVariable String province){
        return ecoleRepository.findCommunesByProvince(province);
    }
    @GetMapping("/liste/{commune}")
    public List<Map<String, Object>> getEcoleByCommune(@PathVariable String commune) {
        return ecoleRepository.findEcolesByCommune(commune)
                .stream()
                .map(ecole -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", ecole.getId());
                    map.put("nom", ecole.getNom());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/count")
    public Long getCount(){
        return ecoleRepository.count();
    }

}




