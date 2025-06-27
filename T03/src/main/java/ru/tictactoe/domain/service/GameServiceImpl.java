package ru.tictactoe.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.tictactoe.datasource.mapper.MapperDomainDataSourceGame;
import ru.tictactoe.datasource.model.GameDataSourceDto;
import ru.tictactoe.datasource.repository.GameRepository;
import ru.tictactoe.datasource.repository.UserRepository;
import ru.tictactoe.domain.model.GameServiceDto;
import ru.tictactoe.domain.model.GameState;
import ru.tictactoe.domain.model.PlayingField;
import ru.tictactoe.domain.model.UserServiceDto;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/** Реализация сервиса игровой логики */
public class GameServiceImpl implements GameService {

    /** Константы для обозначения клеток */
    private final int EMPTY = 0;
    private final int PLAYER = 1;
    private final int COMPUTER = 2;  // или второй игрок

    /** Хранилища данных об играх и пользователях */
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    /** Переводчик моделей игры для сервиса и для базы данных */
    private final MapperDomainDataSourceGame mapperGame;

    @Autowired
    public GameServiceImpl(GameRepository gameRepository, UserRepository userRepository,
                           MapperDomainDataSourceGame mapperGame) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.mapperGame = mapperGame;
    }

    @Override
    public GameServiceDto setupGameWithComputer(GameServiceDto currentGame) {
        currentGame.setUuid(UUID.randomUUID());
        currentGame.setPlayingField(new PlayingField(new int[3][3]));
        currentGame.setState(GameState.PLAYER1_TURN);
        currentGame.setPlayerOne(null);
        currentGame.setPlayerTwo(null);
        gameRepository.save(mapperGame.domainToDataSource(currentGame, new GameDataSourceDto()));
        return currentGame;
    }

    @Override
    public GameServiceDto setupGame(GameServiceDto currentGame, UserServiceDto user) {
        currentGame.setUuid(UUID.randomUUID());
        currentGame.setPlayingField(new PlayingField(new int[3][3]));
        currentGame.setState(GameState.WAITING);

        user.setSign('x');
        currentGame.setPlayerOne(user);
        currentGame.setPlayerTwo(null);
        gameRepository.save(mapperGame.domainToDataSource(currentGame, new GameDataSourceDto()));

        return currentGame;
    }

    @Override
    public void joinGame(GameServiceDto currentGame, UserServiceDto user) {
        user.setSign('o');
        currentGame.setPlayerTwo(user);
        currentGame.setState(GameState.PLAYER1_TURN);
        gameRepository.save(mapperGame.domainToDataSource(currentGame, new GameDataSourceDto()));
    }

    @Override
    public GameServiceDto getGameById(UUID gameId) {
        Optional<GameDataSourceDto> gameFromRepository = gameRepository.findById(gameId);
        GameServiceDto currentGame = null;
        if (gameFromRepository.isPresent()) {
            currentGame = mapperGame.dataSourceToDomain(userRepository, gameFromRepository.get(), new GameServiceDto());
        }
        return currentGame;
    }

    @Override
    public ArrayList<UUID> getAllGames(String userId) {
        Iterable<GameDataSourceDto> games = gameRepository.findAll();
        return StreamSupport.stream(games.spliterator(), false)
                .filter(game -> isPlayerInGame(game, userId))
                .map(GameDataSourceDto::getUuid)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<UUID> getWaitingGames(String userId) {
        Iterable<GameDataSourceDto> games = gameRepository.findAll();
        return StreamSupport.stream(games.spliterator(), false)
                .filter(game -> game.getState() == GameState.WAITING)
                .filter(game -> !isPlayerInGame(game, userId))
                .map(GameDataSourceDto::getUuid)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void playerTurn(UUID userId, GameServiceDto currentGame, int i, int j) {
        try {

            if (Objects.equals(gameStatus(currentGame), "DRAW")) {
                currentGame.setState(GameState.DRAW);
                gameRepository.save(mapperGame.domainToDataSource(currentGame, new GameDataSourceDto()));
                return;
            } else if (Objects.equals(gameStatus(currentGame), "PLAYER WIN")) {
                currentGame.setState(GameState.PLAYER1_WIN);
                gameRepository.save(mapperGame.domainToDataSource(currentGame, new GameDataSourceDto()));
                return;
            } else if (Objects.equals(gameStatus(currentGame), "COMPUTER WIN")) {
                currentGame.setState(GameState.PLAYER2_WIN);
                gameRepository.save(mapperGame.domainToDataSource(currentGame, new GameDataSourceDto()));
                return;
            }

            int SIGN = 0;

            if (userId.equals(currentGame.getPlayerOne().getId()) &&
                    currentGame.getState() == GameState.PLAYER1_TURN) {  // ход крестиков
                SIGN = 1;
                currentGame.setState(GameState.PLAYER2_TURN);
            } else if (userId.equals(currentGame.getPlayerTwo().getId()) &&
                    currentGame.getState() == GameState.PLAYER2_TURN) {  // ход ноликов
                SIGN = 2;
                currentGame.setState(GameState.PLAYER1_TURN);
            } else {
                return;
            }
            currentGame.getPlayingField().setElement(i, j, SIGN);
            gameRepository.save(mapperGame.domainToDataSource(currentGame, new GameDataSourceDto()));
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Override
    public boolean playerTurnWithComputer(GameServiceDto currentGame, int i, int j) {
        try {
            currentGame.getPlayingField().setElement(i, j, PLAYER);
            gameRepository.save(mapperGame.domainToDataSource(currentGame, new GameDataSourceDto()));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public GameServiceDto computerTurn(GameServiceDto currentGame) {
        if (!isFullField(currentGame.getPlayingField().getMatrix())) {
            int[] bestMove = getBestMove(currentGame.getPlayingField().getMatrix());
            currentGame.getPlayingField().setElement(bestMove[0], bestMove[1], COMPUTER);
            gameRepository.save(mapperGame.domainToDataSource(currentGame, new GameDataSourceDto()));
        }

        return currentGame;
    }

    @Override
    public boolean isValidGame(GameServiceDto currentGame) {
        return currentGame != null;
    }

    @Override
    public String gameStatus(GameServiceDto currentGame) {
        int[][] matrix = currentGame.getPlayingField().getMatrix();
        int status = checkRows(matrix);

        if (status == 0) {
            status = checkColumns(matrix);
        }

        if (status == 0) {
            status = checkDiagonals(matrix);
        }

        if (status == 0) {
            if (isFullField(matrix)) {
                status = 3;  // continue
            }
        }

        return switch (status) {
            case 1 -> "PLAYER WIN";
            case 2 -> "COMPUTER WIN";
            case 3 -> "DRAW";
            default -> "CONTINUE";
        };

    }

    private boolean isPlayerInGame(GameDataSourceDto game, String userId) {
        return (game.getPlayerOne() != null && userId.equals(game.getPlayerOne().toString())) ||
                (game.getPlayerTwo() != null && userId.equals(game.getPlayerTwo().toString()));
    }

    public int[] getBestMove(int[][] board) {
        int bestValue = Integer.MIN_VALUE;
        int[] bestMove = new int[]{-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = COMPUTER; // Сделать ход
                    int moveValue = minimax(board, 0, false);
                    board[i][j] = EMPTY; // Отменить ход

                    if (moveValue > bestValue) {
                        bestMove[0] = i;
                        bestMove[1] = j;
                        bestValue = moveValue;
                    }
                }
            }
        }
        return bestMove;
    }

    private int minimax(int[][] board, int depth, boolean isMax) {
        int score = evaluate(board);

        if (score == 10) {  // computer wins
            return score - depth;
        }
        if (score == -10) {  // player wins
            return score + depth;
        }
        if (isFullField(board)) {  // draw
            return 0;
        }

        if (isMax) {
            int best = Integer.MIN_VALUE;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = COMPUTER;
                        best = Math.max(best, minimax(board, depth + 1, !isMax));
                        board[i][j] = EMPTY;
                    }
                }
            }
            return best;

        } else {
            int best = Integer.MAX_VALUE;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = PLAYER;
                        best = Math.min(best, minimax(board, depth + 1, !isMax));
                        board[i][j] = EMPTY;
                    }
                }
            }

            return best;
        }
    }

    private int evaluate(int[][] board) {
        int winner = checkRows(board);

        if (winner == 0) {
            winner = checkColumns(board);
        }
        if (winner == 0) {
            winner = checkDiagonals(board);
        }

        if (winner == PLAYER) {
            return -10;
        } else if (winner == COMPUTER) {
            return 10;
        } else {
            return 0;
        }
    }

    private int checkRows(int[][] matrix) {
        int gameResult = 0;  // draw

        for (int i = 0; i < 3; i++) {
            if (matrix[i][0] != EMPTY) {
                if (matrix[i][0] == matrix[i][1] && matrix[i][1] == matrix[i][2]) {
                    if (matrix[i][0] == PLAYER) {
                        gameResult = PLAYER;  // player win
                    } else {
                        gameResult = COMPUTER;  // computer win
                    }
                }
            }
        }

        return gameResult;
    }

    private int checkColumns(int[][] matrix) {
        int gameResult = 0;  // draw

        for (int j = 0; j < 3; j++) {
            if (matrix[0][j] != EMPTY) {
                if (matrix[0][j] == matrix[1][j] && matrix[1][j] == matrix[2][j]) {
                    if (matrix[0][j] == PLAYER) {
                        gameResult = PLAYER;
                    } else {
                        gameResult = COMPUTER;
                    }
                }
            }
        }

        return gameResult;
    }

    private int checkDiagonals(int[][] matrix) {
        int gameResult = 0;  // draw

        if ((matrix[0][0] == matrix[1][1] && matrix[1][1] == matrix[2][2]) ||
        (matrix[0][2] == matrix[1][1] && matrix[1][1] == matrix[2][0])) {
            if (matrix[1][1] == PLAYER) {
                gameResult = PLAYER;
            } else if (matrix[1][1] == COMPUTER) {
                gameResult = COMPUTER;
            }
        }

        return gameResult;
    }

    private boolean isFullField(int[][] matrix) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (matrix[i][j] == EMPTY) {
                    return false;
                }
            }
        }

        return true;
    }

}
