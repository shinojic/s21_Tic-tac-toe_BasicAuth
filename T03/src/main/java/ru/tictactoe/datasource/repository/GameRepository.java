package ru.tictactoe.datasource.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.tictactoe.datasource.model.GameDataSourceDto;

import java.util.UUID;

/**
 * Spring Data автоматически сгенерирует реализацию
 * методов репозитория по их названию (например, "findBy" + "Id").
 */
@Repository
public interface GameRepository extends CrudRepository<GameDataSourceDto, UUID> {
}
