package ru.astondevs.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = KafkaConfig.class)
@EnableConfigurationProperties
class KafkaConfigTest {

    @Test
    void kafkaConfig_ShouldLoadProperties() {

    }
}