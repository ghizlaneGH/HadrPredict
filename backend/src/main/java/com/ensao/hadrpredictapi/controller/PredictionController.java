package com.ensao.hadrpredictapi.controller;

import com.ensao.hadrpredictapi.dto.PredictionResponse;
import com.ensao.hadrpredictapi.entity.Eleve;
import com.ensao.hadrpredictapi.service.EleveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/predict")
public class PredictionController {

    @Autowired
    private EleveService eleveService;

    @GetMapping("/{eleveId}")
    public ResponseEntity<?> predict(@PathVariable Long eleveId) {
        try {
            PredictionResponse prediction = eleveService.predictAbandon(eleveId);
            return ResponseEntity.ok(prediction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

