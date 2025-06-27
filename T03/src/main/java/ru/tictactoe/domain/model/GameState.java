package ru.tictactoe.domain.model;

public enum GameState {

    WAITING,
    PLAYER1_TURN,  // 'x'
    PLAYER2_TURN,  // 'o'
    PLAYER1_WIN,
    PLAYER2_WIN,
    DRAW

}
