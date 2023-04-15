package com.rua.config;

import com.rua.property.ChamberProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(ChamberProperties.class)
@PropertySource("classpath:chamber.properties")
@RequiredArgsConstructor
public class ChamberConfig {

    private final ChamberProperties chamberProperties;

}