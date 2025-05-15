package ru.astondevs.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.kafka.topics")
public class KafkaConfig {
    private String userAdd;
    private String userDelete;

    @PostConstruct
    public void logConfig() {
        System.out.println("KafkaConfig initialized with userAdd: " + userAdd + ", userDelete: " + userDelete);
    }
}