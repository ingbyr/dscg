package com.ingbyr.hwsc.webui.service;

import com.ingbyr.hwsc.planner.Planner;
import com.ingbyr.hwsc.planner.PlannerConfig;
import com.ingbyr.hwsc.webui.dao.PlannerDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PlannerService {

    private final PlannerDao plannerDao;

    @Autowired
    public PlannerService(PlannerDao plannerDao) {
        this.plannerDao = plannerDao;
    }

    public void saveConfig(PlannerConfig config) {
        plannerDao.saveConfig(config);
    }

    public PlannerConfig loadConfig() {
        return plannerDao.loadConfig();
    }

    public void exec(Planner planner, PlannerConfig config) {
        // Save config to database
//        planner.setup(config);
//        planner.exec();
    }
}
