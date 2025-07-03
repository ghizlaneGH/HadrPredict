package com.ensao.hadrpredictapi.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor


public class Eleve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_eleve", nullable = false, unique = true)
    private Long idEleve;

    @Column(name = "date_de_naissance" ,nullable = false)
    private LocalDate dateDeNaissance;

    @Column(name = "situation")
    private String situation;

    @Column(name = "genre",nullable = false)
    private String genre;

    @Column(name = "classe")
    private String classe;

    @Column(name = "cycle")
    private String cycle;

    @Column(name = "absence")
    private Integer absence;

    @Column(name = "resultat")
    private Double resultat;

    @Column(name = "prediction")
    private Integer prediction;

    @Column(name = "probability_abandon")
    private Double probabilityAbandon;


    @ManyToOne
    @JoinColumn(name = "id_ecole", nullable = false)
    private Ecole ecole;




}

