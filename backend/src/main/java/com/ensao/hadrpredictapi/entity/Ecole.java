package com.ensao.hadrpredictapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"nom","type_school", "commune"}))
public class Ecole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="nom",nullable = false)
    private String nom;

    @Column(name = "type_school",nullable = false)
    private String typeSchool;

    @Column(name = "province",nullable = false)
    private String province; // supprimé unique = true

    @Column(name="commune",nullable = false)
    private String commune;

    @Column(name = "milieu",nullable = false)
    private String milieu;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @OneToMany(mappedBy = "ecole", cascade = CascadeType.ALL)
    private List<Eleve> eleves;

    public Ecole(String nom) {
        this.nom = nom;
    }
}
