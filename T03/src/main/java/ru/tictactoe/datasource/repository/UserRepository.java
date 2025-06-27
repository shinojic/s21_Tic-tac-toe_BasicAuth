package ru.tictactoe.datasource.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.tictactoe.datasource.model.UserDataSourceDto;

import java.util.UUID;

/**
 * Spring Data автоматически сгенерирует реализацию
 * методов репозитория по их названию (например, "findBy" + "Login").
 */
@Repository
public interface UserRepository extends CrudRepository<UserDataSourceDto, UUID> {

    UserDataSourceDto findByLogin(String login);

}
