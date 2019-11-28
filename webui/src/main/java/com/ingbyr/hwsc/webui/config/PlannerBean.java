package com.ingbyr.hwsc.webui.config;

import com.ingbyr.hwsc.planner.Planner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class PlannerBean {

    @Bean
    @Scope("prototype")
    public Planner planner() {
        return new Planner();
    }

}
