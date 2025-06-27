package ru.tictactoe.web.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class GameWebDto {

    private PlayingField playingField;  // char[9]
    private UUID uuid;

}
