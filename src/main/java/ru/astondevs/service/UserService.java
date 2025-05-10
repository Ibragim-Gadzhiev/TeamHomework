package ru.astondevs.service;

import java.util.List;
import ru.astondevs.dto.UserCreateDto;
import ru.astondevs.dto.UserResponseDto;
import ru.astondevs.dto.UserUpdateDto;


public interface UserService {
    UserResponseDto createUser(UserCreateDto dto);
    UserResponseDto getUserById(Long id);
    List<UserResponseDto> getAllUsers();
    UserResponseDto updateUser(Long id, UserUpdateDto dto);
    void deleteById(Long id);
}
