package ru.tictactoe.di;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.tictactoe.datasource.mapper.MapperDomainDataSourceGame;
import ru.tictactoe.datasource.mapper.MapperDomainDataSourceUser;
import ru.tictactoe.datasource.repository.GameRepository;
import ru.tictactoe.datasource.repository.UserRepository;
import ru.tictactoe.domain.service.GameService;
import ru.tictactoe.domain.service.GameServiceImpl;

@Configuration
public class GameConfiguration {

    @Bean
    public MapperDomainDataSourceGame mapperDomainDataSourceGame() {
        return new MapperDomainDataSourceGame();
    }

    @Bean
    public MapperDomainDataSourceUser mapperDomainDataSourceUser(PasswordEncoder encoder) {
        return new MapperDomainDataSourceUser(encoder);
    }

    @Bean
    public GameService gameService(GameRepository gameRepository, UserRepository userRepository,
                                   MapperDomainDataSourceGame mapperGame) {
        return new GameServiceImpl(gameRepository, userRepository, mapperGame);
    }

}
