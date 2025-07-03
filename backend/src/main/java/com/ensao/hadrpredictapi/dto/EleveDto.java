package com.ensao.hadrpredictapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EleveDto {
    private Long id;
    private Long idEleve;
    private LocalDate dateDeNaissance;
    private String situation;
    private String genre;
    private String classe;
    private String cycle;
    private Integer absence;
    private Double resultat;

    // Association à l’école
    private Long idEcole; // uniquement l'ID pour éviter de renvoyer toute l'entité
    private String nomEcole; //pour l'affichage côté frontend
}
