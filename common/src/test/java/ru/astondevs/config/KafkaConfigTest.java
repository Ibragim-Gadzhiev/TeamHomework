package ru.astondevs.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        classes = TestConfig.class,
        properties = "spring.config.import=classpath:kafka.yml,classpath:email.yml"
)
class KafkaConfigTest {
    @Autowired
    private KafkaConfig kafkaConfig;

    @Test
    void testKafkaConfigProperties() {
        assertEquals("userAdd-topic", kafkaConfig.getUserAdd());
        assertEquals("userDelete-topic", kafkaConfig.getUserDelete());
    }
}