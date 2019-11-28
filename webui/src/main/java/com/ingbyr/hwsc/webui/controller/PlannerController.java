package com.ingbyr.hwsc.webui.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ingbyr.hwsc.planner.Planner;
import com.ingbyr.hwsc.planner.PlannerConfig;
import com.ingbyr.hwsc.planner.exception.DAEXConfigException;
import com.ingbyr.hwsc.webui.service.PlannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("planner")
public class PlannerController {

    private final PlannerService plannerService;

    private Planner planner;

    @Autowired
    public PlannerController(PlannerService plannerService,
                             Planner planner) {
        this.plannerService = plannerService;
        this.planner = planner;
    }

    @PostMapping("/run")
    public void exec(PlannerConfig plannerConfig, HttpServletResponse response) throws IOException, DAEXConfigException {
        log.debug("{}", plannerConfig);
        plannerService.exec(planner, plannerConfig);
        response.sendRedirect("/");
    }

    @PostMapping("/config/save")
    public void saveConfig(PlannerConfig plannerConfig) throws JsonProcessingException {
        plannerService.saveConfig(plannerConfig);
    }

    @GetMapping("/config")
    public ResponseEntity<PlannerConfig> loadConfig() throws IOException {
        PlannerConfig plannerConfig = plannerService.loadConfig();
        if (plannerConfig == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(plannerConfig);
        }
    }
}
