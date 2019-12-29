package com.ingbyr.hwsc.webui.config;

import com.ingbyr.hwsc.common.DataSetReader;
import com.ingbyr.hwsc.common.XMLDataSetReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatasetConfig {
    @Bean
    DataSetReader xmlDatasetReader() {
        return new XMLDataSetReader();
    }
}
