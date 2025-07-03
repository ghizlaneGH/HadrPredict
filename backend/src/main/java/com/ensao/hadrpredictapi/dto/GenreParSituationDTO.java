package com.ensao.hadrpredictapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreParSituationDTO {
    private String genre;
    private String situation;
    private Long count;
}
