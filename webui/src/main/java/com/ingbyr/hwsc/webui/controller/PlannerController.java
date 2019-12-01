package com.ingbyr.hwsc.webui.controller;

import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.planner.PlannerAnalyzer;
import com.ingbyr.hwsc.planner.PlannerConfig;
import com.ingbyr.hwsc.planner.exception.DAEXConfigException;
import com.ingbyr.hwsc.webui.service.DatasetService;
import com.ingbyr.hwsc.webui.service.PlannerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PlannerController {

    private final PlannerService plannerService;

    private final DatasetService datasetService;


    @Autowired
    public PlannerController(PlannerService plannerService,
                             DatasetService datasetService) {
        this.plannerService = plannerService;
        this.datasetService = datasetService;
    }

    @ApiOperation("Exec planner")
    @MessageMapping("/exec")
    @SendTo("/topic/result")
    public PlannerAnalyzer exec(@ApiParam(value = "Planner config") PlannerConfig plannerConfig) throws DAEXConfigException {
        log.debug("Load {}", plannerConfig);
        Dataset dataset = plannerConfig.getDataset();
        if (datasetService.needLoadDataset(dataset)) {
            log.debug("Need to reload dataset {}", dataset);
            datasetService.resetDataset(dataset);
        }
        plannerService.saveConfig(plannerConfig);
        return plannerService.exec(plannerConfig);
    }

    @ApiOperation("Get current planner config")
    @GetMapping("/planner/config")
    public ResponseEntity<PlannerConfig> loadConfig() {
        PlannerConfig plannerConfig = plannerService.loadConfig();
        if (plannerConfig == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(plannerConfig);
        }
    }
}
