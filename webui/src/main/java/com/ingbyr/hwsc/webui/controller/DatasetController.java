package com.ingbyr.hwsc.webui.controller;

import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.webui.service.DatasetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisCommands;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("dataset")
@Slf4j
@Api(tags = "Dataset controller")
public class DatasetController {

    private final DatasetService datasetService;

    private final RedisCommands redisCommands;

    @Autowired
    public DatasetController(DatasetService datasetService,
                             RedisCommands redisCommands) {
        this.datasetService = datasetService;
        this.redisCommands = redisCommands;
    }

    @ApiOperation("Clear database")
    @GetMapping("/clear")
    String clearData() {
        redisCommands.flushDb();
        return "Cleared db";
    }

    @ApiOperation("Reload dataset to database")
    @GetMapping("/reload/{dataset}")
    String reloadData(@ApiParam(value = "Dataset id", example = "wsc2009_01") @PathVariable(value = "dataset") String dataset) {
        redisCommands.flushDb();
        datasetService.resetDataset(Dataset.valueOf(dataset.toLowerCase()));
        return "Loaded dataset to db";
    }

}
