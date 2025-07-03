package com.ensao.hadrpredictapi.repository;

import com.ensao.hadrpredictapi.entity.Ecole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EcoleRepository extends JpaRepository<Ecole, Long> {
    Optional<Ecole> findByNomAndCommune(String nom, String commune);

    // recuperer les provinces
    @Query("SELECT DISTINCT e.province FROM Ecole e")
    List<String> findAllProvinces();

    // recuperer les communes par province
    @Query("SELECT DISTINCT e.commune FROM Ecole e WHERE e.province = ?1")
    List<String> findCommunesByProvince(String province);

    // recuperer les etablissements par commune
    List<Ecole> findEcolesByCommune(String commune);

}
