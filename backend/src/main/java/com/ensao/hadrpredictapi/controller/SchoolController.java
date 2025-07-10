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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ecole non trouvée"));

        String cycle = "Inconnu";
        if (ecole.getEleves() != null && !ecole.getEleves().isEmpty()) {
            cycle = ecole.getEleves().get(0).getCycle();
        }

        List<Eleve> eleves = ecole.getEleves();
        long total = eleves.size();

        // --- Statistiques globales ---
        long pred1 = eleves.stream().filter(e -> e.getPrediction() == 1).count();
        long pred0 = total - pred1;

        // --- Calcul du barData : Filles/Garçons parmi les abandons ---
        List<Eleve> abandons = eleves.stream()
                .filter(e -> e.getPrediction() == 1)
                .toList();

        long fillesAbandon = abandons.stream()
                .filter(e -> e.getGenre().equalsIgnoreCase("Fille"))
                .count();

        long garconsAbandon = pred1 - fillesAbandon;

        double pctFilles = pred1 == 0 ? 0 : Math.round(((double) fillesAbandon / pred1) * 1000) / 10.0;
        double pctGarcons = pred1 == 0 ? 0 : Math.round(((double) garconsAbandon / pred1) * 1000) / 10.0;

        // --- Construction de la réponse ---
        Map<String, Object> map = new HashMap<>();
        map.put("SchoolName", ecole.getNom());
        map.put("cycle", cycle);
        map.put("totalEleves", total);

        // Données pour PieChart
        List<Map<String, Object>> pieData = List.of(
                Map.of("name", "Exerce ces études", "value", pred0),
                Map.of("name", "Abandons", "value", pred1)
        );
        map.put("pieData", pieData);

        // Données pour BarChart (modifiées)
        List<Map<String, Object>> barData = List.of(
                Map.of("genre", "Filles", "pourcent", pctFilles),
                Map.of("genre", "Garçons", "pourcent", pctGarcons)
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

    @GetMapping("/map")
    public ResponseEntity<List<Ecole>> getAllWithCoordinates() {
        List<Ecole> dtos = schoolService.getSchoolsWithCoordinates();
        return ResponseEntity.ok(dtos);
    }
}

