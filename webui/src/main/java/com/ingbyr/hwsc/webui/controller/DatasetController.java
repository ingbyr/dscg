package com.ingbyr.hwsc.webui.controller;

import com.ingbyr.hwsc.webui.model.MemoryDatasetReader;
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
@Api(tags = "dataset controller")
public class DatasetController {

    private final MemoryDatasetReader memoryDatasetReader;

    @Autowired
    public DatasetController(MemoryDatasetReader memoryDatasetReader) {
        this.memoryDatasetReader = memoryDatasetReader;
    }

    @ApiOperation("Clear dataset")
    @GetMapping("/clear")
    String clearData() {
        return "Cleared dataset";
    }

    @ApiOperation("Reload dataset to database")
    @GetMapping("/reload/{dataset}")
    String reloadData(@ApiParam(value = "Dataset id", example = "wsc2009_01") @PathVariable(value = "dataset") String dataset) {

        return "Reloaded dataset";
    }

    @GetMapping("")
    String dataset() {
        return memoryDatasetReader.toString();
    }

}
