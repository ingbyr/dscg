package com.ingbyr.hwsc.webui.service;


import org.springframework.data.redis.connection.RedisCommands;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final RedisCommands redisCommands;

    public RedisService(RedisCommands redisCommands) {
        this.redisCommands = redisCommands;
    }

    public void deleteAll() {
        redisCommands.flushAll();
    }

}

