package com.ingbyr.hwsc.webui.controller;

import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.planner.PlannerConfig;
import com.ingbyr.hwsc.webui.service.PlannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Slf4j
@Controller
public class IndexController {

    private final PlannerService plannerService;

    @Autowired
    public IndexController(PlannerService plannerService) {
        this.plannerService = plannerService;
    }

    @GetMapping("/")
    public String hello(Model model) {
        model.addAttribute("all_dataset", Dataset.values());
        try {
            PlannerConfig config = plannerService.loadConfig();
            model.addAttribute("planner_config", config);
            log.debug("Load planner config {}", config);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "index";
    }
}
