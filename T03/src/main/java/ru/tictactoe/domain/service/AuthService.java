package ru.tictactoe.domain.service;

import ru.tictactoe.domain.model.SignUpRequest;
import ru.tictactoe.domain.model.UserServiceDto;

import java.util.UUID;

public interface AuthService {

    /**
     * Регистрация нового пользователя
     * @param signUpRequest Логин, пароль
     * @return Успешная регистрация
     */
    boolean registration(SignUpRequest signUpRequest);

    /**
     * Авторизация существующего пользователя
     * @param loginPassword base64(login:password)
     * @return UUID если пользователь найден, иначе null
     */
    UUID authorization(String loginPassword);

    /**
     * Получение пользователя по UUID
     * @param id UUID не null
     * @return Сервисная модель пользователя если найден, иначе null
     */
    UserServiceDto getUserById(UUID id);
}
