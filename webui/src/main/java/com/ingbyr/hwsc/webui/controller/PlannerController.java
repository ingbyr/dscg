package com.ingbyr.hwsc.webui.controller;

import com.ingbyr.hwsc.planner.PlannerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("planner")
public class PlannerController {

    @PostMapping("/run")
    public void runPlanner(PlannerConfig plannerConfig, HttpServletResponse response) throws IOException {
        log.debug("{}", plannerConfig);
        response.sendRedirect("/");
    }
}
