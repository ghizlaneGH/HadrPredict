package com.ensao.hadrpredictapi.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ClassGenreStatsDto {

    private String classe;
    private String genre;
    private double pourcentage;

}
