package com.ingbyr.hwsc.webui.controller;

import com.ingbyr.hwsc.dataset.Dataset;
import com.ingbyr.hwsc.webui.service.DatasetService;
import com.ingbyr.hwsc.webui.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("dataset")
@Slf4j
@Api(tags = "Control local dataset and database")
public class DatasetController {

    private DatasetService datasetService;

    private RedisService redisService;

    @Autowired
    public DatasetController(DatasetService datasetService,
                             RedisService redisService) {
        this.datasetService = datasetService;
        this.redisService = redisService;
    }

    @ApiOperation("Clear database")
    @GetMapping("/clear")
    String clearData() {
        redisService.deleteAll();
        return "Cleared db";
    }

    @ApiOperation("Reload dataset to database")
    @GetMapping("/reload/{dataset}")
    String reloadData(@ApiParam(value = "Dataset id", example = "wsc2009_01") @PathVariable(value = "dataset") String dataset) {
        redisService.deleteAll();
        datasetService.saveDatasetToDatabase(Dataset.valueOf(dataset.toLowerCase()));
        return "Loaded dataset to db";
    }
}
