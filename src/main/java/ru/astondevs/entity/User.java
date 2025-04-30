package ru.astondevs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Сущность, представляющая пользователя в базе данных.
 * Соответствует таблице "users" в базе данных.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    /**
     * Имя пользователя.
     * Должно содержать 2-50 символов и не быть пустым.
     */
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
    @Column(nullable = false)
    private String name;

    /**
     * Электронная почта пользователя.
     * Должна быть уникальной, соответствовать формату email и не может быть пустой.
     */
    @Email(message = "Некорректный формат Email")
    @NotBlank(message = "Email не может быть пустым")
    @Column(nullable = false)
    private String email;

    /**
     * Возраст пользователя.
     * Должен быть в диапазоне от 0 до 120 лет.
     */
    @Min(value = 0, message = "Возраст не может быть отрицательным")
    @Max(value = 120, message = "Возраст не может превышать 120 лет")
    private int age;

    /**
     * Дата и время создания записи о пользователе.
     * Автоматически устанавливается при создании и не изменяется в дальнейшем.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Конструктор по умолчанию, необходимый для JPA.
     */
    public User() {
    }

    /**
     * Возвращает уникальный идентификатор пользователя.
     *
     * @return идентификатор пользователя
     */
    public Long getId() {
        return id;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param name имя пользователя (2-50 символов)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает электронную почту пользователя.
     *
     * @return email пользователя
     */
    public String getEmail() {
        return email;
    }

    /**
     * Устанавливает электронную почту пользователя.
     *
     * @param email корректный email адрес
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Возвращает возраст пользователя.
     *
     * @return возраст (0-150 лет)
     */
    public int getAge() {
        return age;
    }

    /**
     * Устанавливает возраст пользователя.
     *
     * @param age возраст (должен быть в диапазоне 0-120)
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Возвращает дату и время создания записи.
     * Значение устанавливается автоматически при создании сущности.
     *
     * @return дата создания
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}