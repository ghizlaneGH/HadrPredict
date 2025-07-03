package com.ensao.hadrpredictapi.repository;

import com.ensao.hadrpredictapi.entity.Eleve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EleveRepository extends JpaRepository<Eleve, Long> {

    Optional<Eleve> findByIdEleve(Long idEleve);

    //recuperer les eleves avec prediction
    @Query(value = "SELECT " +
            "AVG(CASE WHEN genre = 'Fille' AND prediction = 1 THEN 100.0 ELSE 0 END) AS pctF, " +
            "AVG(CASE WHEN genre = 'Garçon' AND prediction = 1 THEN 100.0 ELSE 0 END) AS pctM " +
            "FROM eleve", nativeQuery = true)
    List<Object[]> countByPrediction();

    @Query("SELECT  COUNT(*) FROM Eleve WHERE genre ='Fille' and prediction=1")
    Long countFillePrediction();

    @Query("SELECT COUNT(*) FROM Eleve WHERE genre ='Garçon' and prediction=1")
    Long countGarconPrediction();

    @Query("SELECT COUNT(*) FROM Eleve WHERE genre='Fille'")
    Long countTFille();

    @Query("SELECT COUNT(*) FROM Eleve WHERE genre='Garçon'")
    Long countTGarcon();

    //recuperer les eleves par genre
    @Query("SELECT e.genre, COUNT(e.id) FROM Eleve e GROUP BY e.genre")
    List<Object[]> countByGenreGroupedList();



    // recuperer les eleves par genre et par classe
    @Query("SELECT e.classe, e.genre, COUNT(e.id) FROM Eleve e GROUP BY e.classe, e.genre")
    List<Object[]> countByClasseAndGenreGroupedList();



    @Modifying
    @Query("DELETE FROM Eleve")
    void deleteAllByIdNotNull();

    // recupere les eleves appartiennent a un etablissement
    List<Eleve> findByEcoleId(Long id_Ecole);

}