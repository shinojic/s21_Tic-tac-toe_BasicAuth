package ru.tictactoe.datasource.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access= AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
public class UserDataSourceDto {

    @Id
    @Column(updatable = false, nullable = false, unique = true)
    private final UUID uuid;

    @Column(updatable = false, nullable = false, unique = true, length = 20)
    private final String login;

    @Column(nullable = false)
    private String password;

}
