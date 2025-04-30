package ru.astondevs.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.astondevs.entity.User;

/**
 * Репозиторий для управления сущности.
 *
 * <p>Наследуется от {@link JpaRepository} и предоставляет стандартные
 * операции работы с базой данных, а также дополнительные методы для
 * поиска и проверки наличия пользователя по адресу электронной почты</p>
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Ищет пользователя по адресу электронной почты.
     *
     * @param email адрес электронной почты пользователя
     * @return {@link Optional}, содержащий пользователя, если найден
     */
    Optional<User> findByEmail(String email);

    /**
     * Проверяет, существует ли пользователь с указанным адресом электронной почты.
     *
     * @param email адрес электронной почты пользователя.
     * @return {@code true}, если пользователь с таким email существует
     *         {@code false}, если пользователь с таким email не существует
     */
    boolean existsByEmail(String email);
}
