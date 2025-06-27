package ru.tictactoe.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.tictactoe.datasource.repository.UserRepository;
import ru.tictactoe.domain.model.AuthFilter;
import ru.tictactoe.domain.service.AuthService;
import ru.tictactoe.domain.service.AuthServiceImpl;

@Configuration
public class SecurityConfiguration {

    /**
     * Кастомный фильтр (extends GenericFilterBean)
     */
    @Bean
    public AuthFilter authFilter(AuthService authService, UserRepository userRepository) {
        return new AuthFilter(authService(userRepository));
    }

    /**
     * Сервис обработки запросов на регистрацию и авторизацию
     */
    @Bean
    public AuthService authService(UserRepository userRepository) {
        return new AuthServiceImpl(userRepository, passwordEncoder());
    }

    /**
     * Шифровщик (алгоритм хеширования bcrypt)
     * Пароли хранятся в базе данных в зашифрованном виде и их нельзя дешифровать.
     * Введенный пароль шифруется с использованием того же алгоритма,
     * а затем сравнивается с шифрованным паролем в базе данных.
     * Сравнение выполняет метод matches() объекта PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Настройка цепочки безопасности
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthFilter authFilter) throws Exception {
        http
                // Настраиваем доступ к страницам без авторизации
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/tic-tac-toe/registration", "/tic-tac-toe/auth").permitAll()
                        .anyRequest().authenticated())

                // Отключаем CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // Добавляем кастомный фильтр
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)

                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        // собрать цепочку фильтров
        return http.build();
    }

}
