package ru.astondevs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для создания нового пользователя.
 *
 * <p>Используется для передачи данных при создании нового пользователя</p>
 * <ul>
 *     <li><b>Имя</b> – должно быть 2-50 символов, не должно быть пустым</li>
 *     <li><b>Email</b> – должен быть корректным и не быть пустым</li>
 *     <li><b>Возраст</b> от 0 до 120 лет</li>
 * </ul>
 */
public class UserCreateDto {
    @NotBlank(message = "{name.not.found}")
    @Size(min = 2, max = 50, message = "{name.incorrect}")
    private String name;

    @NotBlank(message = "{email.not.found}")
    @Email(message = "{email.incorrect}")
    private String email;

    @Min(value = 0, message = "{min.age}") @Max(value = 120, message = "{max.age}")
    private int age;

    /**
     * Возвращает имя пользователя.
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя пользователя.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает адрес электронной почты пользователя.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Устанавливает адрес электронной почты пользователя.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Возвращает возраст пользователя.
     */
    public int getAge() {
        return age;
    }

    /**
     * Устанавливает возраст пользователя.
     */
    public void setAge(int age) {
        this.age = age;
    }
}
