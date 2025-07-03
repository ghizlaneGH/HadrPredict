package com.ensao.hadrpredictapi.controller;

import com.ensao.hadrpredictapi.dto.ClassGenreStatsDto;
import com.ensao.hadrpredictapi.dto.GenreStatsDto;
import com.ensao.hadrpredictapi.service.DashboardGService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardGService dashboardGService;
    @GetMapping("/piechart")
    public List<GenreStatsDto> getPieChartData() {
        return dashboardGService.getGenreStats();
    }

    @GetMapping("/barchart")
    public List<ClassGenreStatsDto> getBarChartData() {
        return dashboardGService.getClassGenreStats();
    }


}
