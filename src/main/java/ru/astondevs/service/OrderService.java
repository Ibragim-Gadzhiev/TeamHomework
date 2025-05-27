package ru.astondevs.service;


import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.feign.Resilience4jFeign;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class OrderService {

    private final UserClient userClient;

    public OrderService() {
        // Создаём конфигурацию Circuit Breaker
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)  // порог ошибок (%)
                .waitDurationInOpenState(Duration.ofSeconds(5)) // время ожидания перед переходом в HALF_OPEN
                .slidingWindowSize(10)     // количество запросов для статистики
                .build();

        CircuitBreaker circuitBreaker = CircuitBreaker.of("user-service", config);

        // Декорируем клиент
        this.userClient = Resilience4jFeign.builder()
                .withCircuitBreaker(circuitBreaker)
                .target(UserClient.class, "http://user-service");
    }

    public String getUserInfo(Long userId) {
        return userClient.getUserById(userId);
    }
}
