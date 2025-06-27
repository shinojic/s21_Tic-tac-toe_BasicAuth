package ru.tictactoe.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class GameServiceDto {

    private UUID uuid;
    private GameState state;
    private PlayingField playingField;  // int[3][3]

    private UserServiceDto playerOne;  // 'x'
    private UserServiceDto playerTwo;  // 'o'

}
