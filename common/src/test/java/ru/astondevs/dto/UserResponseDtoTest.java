package ru.astondevs.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class UserResponseDtoTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void serializeAndDeserialize_ShouldWorkCorrectly() throws Exception {
        UserResponseDto dto = new UserResponseDto(
                1L,
                "Ibra",
                "unknown.nvme@gmail.com",
                25,
                LocalDateTime.now()
        );

        String json = objectMapper.writeValueAsString(dto);
        UserResponseDto result = objectMapper.readValue(json, UserResponseDto.class);
        assertThat(result).isEqualTo(dto);
    }
}
