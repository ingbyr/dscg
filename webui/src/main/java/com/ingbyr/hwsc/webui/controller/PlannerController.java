package com.ingbyr.hwsc.webui.controller;

import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.planner.Planner;
import com.ingbyr.hwsc.planner.PlannerConfig;
import com.ingbyr.hwsc.webui.service.DatasetService;
import com.ingbyr.hwsc.webui.service.PlannerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    private final DatasetService datasetService;

    private Planner planner;

    @Autowired
    public PlannerController(PlannerService plannerService,
                             DatasetService datasetService,
                             Planner planner) {
        this.plannerService = plannerService;
        this.datasetService = datasetService;
        this.planner = planner;
    }

    @ApiOperation("Exec planner")
    @PostMapping("/exec")
    public void exec(@ApiParam(value = "Planner config") PlannerConfig plannerConfig, HttpServletResponse response) throws IOException {
        log.debug("Load {}", plannerConfig);
        Dataset dataset = plannerConfig.getDataset();
        if (datasetService.needLoadDataset(dataset)) {
            log.debug("Need to reload dataset {}", dataset);
            datasetService.resetDataset(dataset);
        }
        plannerService.saveConfig(plannerConfig);
        plannerService.exec(planner, plannerConfig);
        response.sendRedirect("/");
    }

    @ApiOperation("Get current planner config")
    @GetMapping("/config")
    public ResponseEntity<PlannerConfig> loadConfig() {
        PlannerConfig plannerConfig = plannerService.loadConfig();
        if (plannerConfig == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(plannerConfig);
        }
    }
}
