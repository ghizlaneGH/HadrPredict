package com.ensao.hadrpredictapi.controller;

import com.ensao.hadrpredictapi.entity.Eleve;
import com.ensao.hadrpredictapi.repository.EleveRepository;
import com.ensao.hadrpredictapi.service.EleveService;
import com.ensao.hadrpredictapi.service.ExcelEleveService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/eleve")
@Slf4j
public class EleveController {

    private final ExcelEleveService excelEleveService;
    private final EleveService eleveService;
    private final EleveRepository eleveRepository;


   // uploader un fichier Excel
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Nom du fichier reçu : " + file.getOriginalFilename());
            excelEleveService.importer(file);
            return ResponseEntity.ok("Fichier des élèves importé avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }

    //Recuperer tous les eleves
    @GetMapping("/all")
    public ResponseEntity<List<Eleve>> getAllEleves() {
        List<Eleve> eleves = eleveService.getAllEleves();
        return ResponseEntity.ok(eleves);
    }

    //Supprimer toutes les donnees (conserver la structure)
    @DeleteMapping("/reset")
    public ResponseEntity<Map<String, String>> resetTable(){
        try {
            eleveService.resetTable();
            return ResponseEntity.ok(Map.of("message", "Donnees supprimees avec succes"));
        }catch (Exception e){
            log.error("Erreur lors de la réinitialisation de la table", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la suppression:" + e.getMessage()));
        }
    }

    //Statistiques groupees par prediction
    @GetMapping("/par-prediction")
    public ResponseEntity<Map<String,Double>> getParPrediction() {
        Map<String,Double> map = eleveService.getPredictionStats();
        return ResponseEntity.ok(map);
    }
    @GetMapping("/pred-par-genre")
    public Map<String,Double> getPredictionParGenre(){
        double totalFilles = eleveRepository.countTFille();
        double totalGarcon = eleveRepository.countTGarcon();

        double fillePred =eleveRepository.countFillePrediction();
        double garconPred =eleveRepository.countGarconPrediction();

        Map<String,Double> map=new HashMap<>();
        map.put("Fille",totalFilles>0 ?(fillePred/totalFilles)*100 :0);
        map.put("Garçon",totalGarcon>0 ? (garconPred/totalGarcon)*100 :0);

    return map;
    }
}


