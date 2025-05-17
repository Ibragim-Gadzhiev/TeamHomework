package ru.astondevs.config;

import org.mockito.Mockito;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "ru.astondevs")
public class TestConfig {

    @Bean
    @Primary
    public JavaMailSender javaMailSender() {
        return Mockito.mock(JavaMailSender.class);
    }
}