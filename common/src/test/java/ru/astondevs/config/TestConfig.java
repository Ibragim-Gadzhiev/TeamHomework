package ru.astondevs.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KafkaConfig.class)
@ConfigurationPropertiesScan("ru.astondevs.config")
public class TestConfig {
}