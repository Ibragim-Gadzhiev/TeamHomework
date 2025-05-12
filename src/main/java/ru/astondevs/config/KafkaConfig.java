package ru.astondevs.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.userAdd}")
    private String userAddTopic;

    @Value("${kafka.topics.userDelete}")
    private String userDeleteTopic;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroupId;
}
