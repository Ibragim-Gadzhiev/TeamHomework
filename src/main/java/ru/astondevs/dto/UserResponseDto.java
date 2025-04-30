package ru.astondevs.dto;

import java.time.LocalDateTime;

/**
 * DTO для представления пользовательских данных в ответе.
 *
 * <p>Используется для возврата информации о пользователе клиенту после создания,
 * обновления или получения данных.</p>
 *
 * <p>Содержит следующие поля:
 * <ul>
 *     <li>{@code id} - уникальный идентификатор пользователя</li>
 *     <li>{@code name} - имя пользователя</li>
 *     <li>{@code email} - адрес электронной почты</li>
 *     <li>{@code age} - возраст</li>
 *     <li>{@code createdAt} - дата и время создания пользователя</li>
 * </ul>
 */
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private int age;
    private LocalDateTime createdAt;

    /**
     * Возвращает идентификатор пользователя.
     */
    public Long getId() {
        return id;
    }

    /**
     * Возвращает имя пользователя.
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает адрес электронной почты пользователя.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Возвращает возраст пользователя.
     */
    public int getAge() {
        return age;
    }

    /**
     * Возвращает время и дату создания пользователя.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Устанавливает идентификатор пользователя.
     */
    void setId(Long id) {
        this.id = id;
    }

    /**
     * Устанавливает имя пользователя.
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Устанавливает адрес электронной почты пользователя.
     */
    void setEmail(String email) {
        this.email = email;
    }

    /**
     * Устанавливает возраст пользователя.
     */
    void setAge(int age) {
        this.age = age;
    }

    /**
     * Устанавливает время и дату создания пользователя.
     */
    void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}