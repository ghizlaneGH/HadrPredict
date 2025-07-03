package com.ensao.hadrpredictapi.service;

import com.ensao.hadrpredictapi.dto.*;
import com.ensao.hadrpredictapi.entity.Eleve;
import com.ensao.hadrpredictapi.entity.Ecole;
import com.ensao.hadrpredictapi.repository.EleveRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class EleveService {

    @Autowired
    private EleveRepository eleveRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    // URL de ton API Python FastAPI
    private final String pythonApiUrl = "http://127.0.0.1:8000/predict";

    private int calculerAge(LocalDate dateNaissance) {
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }
    public PredictionResponse predictAbandon(Long eleveId) throws Exception {
        Eleve eleve = eleveRepository.findById(eleveId)
                .orElseThrow(() -> new Exception("Eleve non trouve"));

        Ecole ecole = eleve.getEcole();

        // Préparer les données à envoyer à l'API Python
        StudentInput input = new StudentInput(
                calculerAge(eleve.getDateDeNaissance()),
                eleve.getGenre(),
                eleve.getResultat(),
                eleve.getAbsence(),
                eleve.getCycle(),
                eleve.getSituation(),
                ecole.getMilieu(),
                ecole.getTypeSchool()
        );

        // Appel POST vers l'API Python
        PredictionResponse prediction = restTemplate.postForObject(pythonApiUrl, input, PredictionResponse.class);

        if (prediction == null) {
            throw new Exception("Erreur lors de la récupération de la prédiction");
        }

        return prediction;
    }

    //recuerer les eleves
    @Transactional(readOnly = true)
    public List<Eleve> getAllEleves() {
        return eleveRepository.findAll();
    }
    //vider la table eleve
    @Transactional
    public void resetTable() {
        eleveRepository.deleteAllInBatch();
    }

    //Statistiques par prediction
    public Map<String,Double> getPredictionStats(){
        Object[] pred=eleveRepository.countByPrediction().get(0);

        double nbrAbandons=((Number)pred[0]).doubleValue();
        double nbrContinue=((Number)pred[1]).doubleValue();
        double total=nbrAbandons+nbrContinue;

        //
        Function<Double,Double> round2Digits = d->Math.round(d*100.0)/100.0;

        Map<String,Double> map=new HashMap<>();
        map.put("Abandons",total==0 ? 0.0 : round2Digits.apply((nbrAbandons / total) *100));
        map.put("Exerce ces etudes",total==0 ? 0.0 : round2Digits.apply((nbrContinue / total) *100));

        return map;
    }

}
