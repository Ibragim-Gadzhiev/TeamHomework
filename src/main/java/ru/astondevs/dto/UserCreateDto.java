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
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно содержать 2-50 символов")
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @Min(value = 0, message = "Возраст не может быть отрицательным")
    @Max(value = 120, message = "Возраст не может быть больше 120 лет")
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
