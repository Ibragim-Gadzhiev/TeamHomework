package ru.astondevs.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Сущность, представляющая пользователя в базе данных.
 * Соответствует таблице "users" в базе данных.
 */
@Entity
@Table(name = "users")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    /**
     * Имя пользователя.
     * Должно содержать 2-50 символов и не быть пустым.
     */
    @NotBlank(message = "{name.not.found}")
    @Size(min = 2, max = 50, message = "{name.incorrect}")
    @Column(nullable = false)
    @Setter
    private String name;

    /**
     * Электронная почта пользователя.
     * Должна быть уникальной, соответствовать формату email и не может быть пустой.
     */
    @Email(message = "{email.incorrect}")
    @NotBlank(message = "{email.not.found}")
    @Column(nullable = false)
    @Setter
    private String email;

    /**
     * Возраст пользователя.
     * Должен быть в диапазоне от 0 до 120 лет.
     */
    @Min(value = 0, message = "{min.age}")
    @Max(value = 120, message = "{max.age}")
    @Setter
    private Integer age;

    /**
     * Дата и время создания записи о пользователе.
     * Автоматически устанавливается при создании и не изменяется в дальнейшем.
     */
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}