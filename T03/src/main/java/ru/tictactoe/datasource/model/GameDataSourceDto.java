package ru.tictactoe.datasource.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import ru.tictactoe.domain.model.GameState;

import java.util.UUID;

@Entity
@Table(name = "game")
@Getter
@Setter
@NoArgsConstructor
public class GameDataSourceDto {

    @Id
    private UUID uuid;

    /** Игровое поле (String - varchar(9)) */
    @Embedded
    private PlayingField playingField;

    /** Состояние игры сохраняется в базе данных как строковое значение */
    @Enumerated(EnumType.STRING)
    private GameState state;

    @Column
    private UUID playerOne;

    @Column
    private UUID playerTwo;

}
