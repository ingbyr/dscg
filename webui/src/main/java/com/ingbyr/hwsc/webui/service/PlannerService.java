package com.ingbyr.hwsc.webui.service;

import com.ingbyr.hwsc.planner.Planner;
import com.ingbyr.hwsc.planner.PlannerAnalyzer;
import com.ingbyr.hwsc.planner.PlannerConfig;
import com.ingbyr.hwsc.planner.exception.DAEXConfigException;
import com.ingbyr.hwsc.webui.dao.PlannerDao;
import com.ingbyr.hwsc.webui.model.MemoryDatasetReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

@Slf4j
@Service
public class PlannerService {

    private final PlannerDao plannerDao;

    private final MemoryDatasetReader memoryDatasetReader;

    @Autowired
    public PlannerService(PlannerDao plannerDao, MemoryDatasetReader memoryDatasetReader) {
        this.plannerDao = plannerDao;
        this.memoryDatasetReader = memoryDatasetReader;
    }

    public void saveConfig(PlannerConfig config) {
        plannerDao.saveConfig(config);
    }

    public PlannerConfig loadConfig() {
        return plannerDao.loadConfig();
    }

    public PlannerAnalyzer exec(PlannerConfig config) throws DAEXConfigException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Planner planner = new Planner();
        planner.setup(config, memoryDatasetReader);
        planner.exec();
        return planner.getAnalyzer();
    }
}
