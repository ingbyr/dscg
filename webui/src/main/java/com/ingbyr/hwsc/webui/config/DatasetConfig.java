package com.ingbyr.hwsc.webui.config;

import com.ingbyr.hwsc.dataset.DataSetReader;
import com.ingbyr.hwsc.dataset.XMLDataSetReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatasetConfig {
    @Bean
    DataSetReader xmlDatasetReader() {
        return new XMLDataSetReader();
    }
}
