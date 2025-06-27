package ru.tictactoe.datasource.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.tictactoe.datasource.model.UserDataSourceDto;
import ru.tictactoe.domain.model.SignUpRequest;
import ru.tictactoe.domain.model.UserServiceDto;

import java.util.UUID;

public class MapperDomainDataSourceUser {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MapperDomainDataSourceUser(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserDataSourceDto domainToDataSource(final SignUpRequest signUpRequest) {
        return new UserDataSourceDto(UUID.randomUUID(),
                signUpRequest.getLogin(), passwordEncoder.encode(signUpRequest.getPassword()));
    }

    public UserServiceDto dataSourceToService(final UserDataSourceDto userDataSourceDto, UserServiceDto userServiceDto) {
        userServiceDto.setId(userDataSourceDto.getUuid());
        userServiceDto.setLogin(userDataSourceDto.getLogin());
        userServiceDto.setSign('-');
        return userServiceDto;
    }

}
