package ru.tictactoe.datasource.mapper;

import ru.tictactoe.datasource.model.GameDataSourceDto;
import ru.tictactoe.datasource.model.PlayingField;
import ru.tictactoe.datasource.model.UserDataSourceDto;
import ru.tictactoe.datasource.repository.UserRepository;
import ru.tictactoe.domain.model.GameServiceDto;
import ru.tictactoe.domain.model.UserServiceDto;

import java.util.Optional;

public class MapperDomainDataSourceGame {

    public GameDataSourceDto domainToDataSource(final GameServiceDto gameService, GameDataSourceDto gameDataSource) {
        if (gameDataSource.getPlayingField() == null) {
            gameDataSource.setPlayingField(new PlayingField());
        }

        StringBuilder matrixString = new StringBuilder();
        int[][] matrixInt = gameService.getPlayingField().getMatrix();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                matrixString.append(matrixInt[i][j]);  // автоматическое преобразование числа в символ
            }
        }

        gameDataSource.getPlayingField().setMatrix(matrixString.toString());
        gameDataSource.setUuid(gameService.getUuid());
        gameDataSource.setState(gameService.getState());

        if (gameService.getPlayerOne() != null) {
            gameDataSource.setPlayerOne(gameService.getPlayerOne().getId());
        } else {
            gameDataSource.setPlayerOne(null);
        }

        if (gameService.getPlayerTwo() != null) {
            gameDataSource.setPlayerTwo(gameService.getPlayerTwo().getId());
        } else {
            gameDataSource.setPlayerTwo(null);
        }

        return gameDataSource;
    }

    public GameServiceDto dataSourceToDomain(UserRepository repository, final GameDataSourceDto gameDataSource, GameServiceDto gameService) {
        if (gameService.getPlayingField() == null) {
            gameService.setPlayingField(new ru.tictactoe.domain.model.PlayingField(new int[3][3]));
        }

        String matrixString = gameDataSource.getPlayingField().getMatrix();
        int[][] matrixInt = gameService.getPlayingField().getMatrix();

        for (int i = 0; i < 9; i++) {
            char c = matrixString.charAt(i);
            int value = c - '0';  // преобразование символа в число
            matrixInt[i / 3][i % 3] = value;
        }

        gameService.getPlayingField().setMatrix(matrixInt);
        gameService.setUuid(gameDataSource.getUuid());
        gameService.setState(gameDataSource.getState());

        if (gameDataSource.getPlayerOne() != null) {
            Optional<UserDataSourceDto> playerOneOpt = repository.findById(gameDataSource.getPlayerOne());
            if (playerOneOpt.isPresent()) {
                UserDataSourceDto playerOne = playerOneOpt.get();
                gameService.setPlayerOne(new UserServiceDto(playerOne.getLogin(), playerOne.getUuid(), 'x'));
            } else {
                return null;
            }
        } else {
            gameService.setPlayerOne(null);
        }

        if (gameDataSource.getPlayerTwo() != null) {
            Optional<UserDataSourceDto> playerTwoOpt = repository.findById(gameDataSource.getPlayerTwo());
            if (playerTwoOpt.isPresent()) {
                UserDataSourceDto playerTwo = playerTwoOpt.get();
                gameService.setPlayerTwo(new UserServiceDto(playerTwo.getLogin(), playerTwo.getUuid(), 'o'));
            } else {
                return null;
            }
        } else {
            gameService.setPlayerTwo(null);
        }

        return gameService;
    }

}
