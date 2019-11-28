package com.ingbyr.hwsc.webui.dao;

import com.ingbyr.hwsc.planner.PlannerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PlannerDao {

    private static final String KEY_PLANNER_CONFIG = "plannerConfig";

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public PlannerDao(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveConfig(PlannerConfig config) {
        log.debug("Save config {}", config);
        redisTemplate.opsForValue().set(KEY_PLANNER_CONFIG, config);
    }

    public PlannerConfig loadConfig() {
        return (PlannerConfig) redisTemplate.opsForValue().get(KEY_PLANNER_CONFIG);
    }
}
