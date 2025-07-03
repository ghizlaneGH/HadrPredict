package com.ensao.hadrpredictapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentInput {
    @JsonProperty("age")
    private int age;
    @JsonProperty("Genre")
    private String Genre;
    @JsonProperty("Resultat")
    private double Resultat;
    @JsonProperty("Absence")
    private int Absence;
    @JsonProperty("Cycle")
    private String Cycle;
    @JsonProperty("Milieu")
    private String Milieu;
    @JsonProperty("Type_etablissement")
    private String Type_etablissement;
    @JsonProperty("Situation")
    private String Situation;


}