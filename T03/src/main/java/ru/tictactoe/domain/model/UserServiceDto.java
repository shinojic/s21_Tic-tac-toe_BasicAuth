package ru.tictactoe.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserServiceDto {

    private String login;
    private UUID id;
    private char sign;  // 'x', 'o'

}
