package com.ensao.hadrpredictapi.service;


import com.ensao.hadrpredictapi.dto.ClassGenreStatsDto;
import com.ensao.hadrpredictapi.dto.GenreStatsDto;
import com.ensao.hadrpredictapi.entity.Eleve;
import com.ensao.hadrpredictapi.repository.EleveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardGService {

    private final EleveRepository eleveRepository;


    //  Retourne les statistiques par genre sous forme de pourcentage.

    public List<GenreStatsDto> getGenreStats() {
        List<Object[]> results = eleveRepository.countByGenreGroupedList();
        long totalStudents = results.stream().mapToLong(obj -> ((Number) obj[1]).longValue()).sum();

        if (totalStudents == 0) return Collections.emptyList();

        return results.stream()
                .map(row -> new GenreStatsDto(
                        row[0].toString(),
                        Math.round(((Number) row[1]).doubleValue() / totalStudents * 1000) / 10.0
                ))
                .collect(Collectors.toList());
    }

    // Retourne les statistiques par classe et genre

    public List<ClassGenreStatsDto> getClassGenreStats() {
        List<Object[]> results = eleveRepository.countByClasseAndGenreGroupedList();

        // Regrouper par classe pour calculer le total par classe
        Map<String, Long> totalPerClass = new HashMap<>();
        for (Object[] row : results) {
            String classe = (String) row[0];
            Long count = ((Number) row[2]).longValue();
            totalPerClass.merge(classe, count, Long::sum);
        }

        if (totalPerClass.isEmpty()) return Collections.emptyList();

        return results.stream()
                .map(row -> {
                    String classe = (String) row[0];
                    String genre = (String) row[1];
                    Long count = ((Number) row[2]).longValue();
                    double percentage = Math.round((double) count / totalPerClass.get(classe) * 1000) / 10.0;
                    return new ClassGenreStatsDto(classe, genre, percentage);
                })
                .collect(Collectors.toList());
    }
}
