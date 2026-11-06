package br.edu.sistemaacademico.controller;

import br.edu.sistemaacademico.dto.DashboardResponse;
import br.edu.sistemaacademico.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Indicadores do painel inicial. */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardResponse resumo() {
        return dashboardService.resumo();
    }
}
