package com.ingbyr.hwsc.webui.controller;

import com.ingbyr.hwsc.common.Dataset;
import com.ingbyr.hwsc.planner.Evaluators;
import com.ingbyr.hwsc.planner.Fitness;
import com.ingbyr.hwsc.planner.PlannerConfig;
import com.ingbyr.hwsc.webui.service.PlannerService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;

@Slf4j
@Controller
public class IndexController {

    private final PlannerService plannerService;

    @Autowired
    public IndexController(PlannerService plannerService) {
        this.plannerService = plannerService;
    }

    @ApiOperation("Index controller")
    @GetMapping("/")
    public String index(Model model) {
        PlannerConfig config = plannerService.loadConfig();
        model.addAttribute("planner_config", config);
        model.addAttribute("dataset_list", Dataset.values());
        System.out.println(Arrays.toString(Evaluators.values()));
        model.addAttribute("evaluators", Evaluators.values());
        model.addAttribute("indicators", Fitness.getAllNames());
        log.debug("Load planner config {}", config);
        return "index";
    }
}
