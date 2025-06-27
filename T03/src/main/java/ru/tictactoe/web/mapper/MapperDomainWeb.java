package ru.tictactoe.web.mapper;

import ru.tictactoe.domain.model.GameServiceDto;
import ru.tictactoe.web.model.GameWebDto;
import ru.tictactoe.web.model.PlayingField;

public class MapperDomainWeb {

    public GameWebDto domainToWeb(final GameServiceDto gameService, GameWebDto gameWeb) {
        if (gameWeb.getPlayingField() == null) {
            gameWeb.setPlayingField(new PlayingField(new char[3][3]));
        }
        gameWeb.getPlayingField().setMatrix(intFieldToChar(gameService.getPlayingField().getMatrix()));
        gameWeb.setUuid(gameService.getUuid());
        return gameWeb;
    }

    public GameServiceDto webToDomain(final GameWebDto gameWeb, GameServiceDto gameService) {
        if (gameService.getPlayingField() == null) {
            gameService.setPlayingField(new ru.tictactoe.domain.model.PlayingField(new int[3][3]));
        }
        gameService.getPlayingField().setMatrix(charFieldToInt(gameWeb.getPlayingField().getMatrix()));
        gameService.setUuid(gameWeb.getUuid());
        return gameService;
    }

    private int[][] charFieldToInt(char[][] charMatrix) {
        int[][] intMatrix = new int[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                intMatrix[i][j] = switch (charMatrix[i][j]) {
                    case 'x' -> 1;
                    case 'o' -> 2;
                    default -> 0;
                };
            }
        }

        return intMatrix;
    }

    private char[][] intFieldToChar(int[][] intMatrix) {
        char[][] charMatrix = new char[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                charMatrix[i][j] = switch (intMatrix[i][j]) {
                    case 1 -> 'x';
                    case 2 -> 'o';
                    default -> '-';
                };
            }
        }

        return charMatrix;
    }

}
