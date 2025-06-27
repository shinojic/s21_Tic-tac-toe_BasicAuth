package ru.tictactoe.domain.service;

import ru.tictactoe.domain.model.GameServiceDto;
import ru.tictactoe.domain.model.UserServiceDto;

import java.util.ArrayList;
import java.util.UUID;

/** Сервис игровой логики */
public interface GameService {

    /**
     * Подготовка игры с другим пользователем
     * @param currentGame Модель игры (пустая)
     * @param user Пользователь, создающий игру
     * @return Модель игры
     */
    GameServiceDto setupGame(GameServiceDto currentGame, UserServiceDto user);

    /**
     * Подготовка игры с компьютером
     * @param currentGame Модель игры (пустая)
     * @return Модель игры
     */
    GameServiceDto setupGameWithComputer(GameServiceDto currentGame);

    /**
     * Присоединение к игре второго игрока
     * @param currentGame Модель игры
     * @param user Второй игрок
     */
    void joinGame(GameServiceDto currentGame, UserServiceDto user);

    /**
     * Получение игры по идентификатору
     * @param gameId game id
     * @return Модель игры
     */
    GameServiceDto getGameById(UUID gameId);

    /**
     * Получение списка игр
     * @param userId user id
     * @return Лист со всеми играми, в которых принимал участие пользователь (кроме игр с компьютером)
     */
    ArrayList<UUID> getAllGames(String userId);

    /**
     * Получение списка игр, ожидающих второго игрока
     * @param userId user id
     * @return Лист со всеми играми в статусе WAITING
     */
    ArrayList<UUID> getWaitingGames(String userId);

    /**
     * Ход игрока при игре с другим пользователем
     * @param userId Текущий игрок
     * @param currentGame Текущая игра
     * @param i индекс строки
     * @param j индекс столбца
     */
    void playerTurn(UUID userId, GameServiceDto currentGame, int i, int j);

    /**
     * Ход игрока при игре с компьютером
     * @param currentGame Текущая игра
     * @param i индекс строки
     * @param j индекс столбца
     * @return true, false в случае некорректных аргументов
     */
    boolean playerTurnWithComputer(GameServiceDto currentGame, int i, int j);

    /**
     * Расчет хода компьютера с использованием алгоритма минимакс
     * @param currentGame Текущая игра
     * @return Обновленная игра
     */
    GameServiceDto computerTurn(GameServiceDto currentGame);

    /**
     * Валидация текущей игры (сравнение с экземпляром в базе данных)
     * @param currentGame Текущая игра
     * @return Валидность игрового поля
     */
    boolean isValidGame(GameServiceDto currentGame);

    /**
     * Проверка на конец игры
     * @param currentGame Текущая игра
     * @return Статус игры (continue, player/computer win, draw)
     */
    String gameStatus(GameServiceDto currentGame);

}
