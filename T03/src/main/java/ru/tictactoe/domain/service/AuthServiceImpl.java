package ru.tictactoe.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.tictactoe.datasource.mapper.MapperDomainDataSourceUser;
import ru.tictactoe.datasource.model.UserDataSourceDto;
import ru.tictactoe.datasource.repository.UserRepository;
import ru.tictactoe.domain.model.SignUpRequest;
import ru.tictactoe.domain.model.UserServiceDto;

import java.util.Optional;
import java.util.UUID;
import java.util.Base64;

/** Реализация сервиса авторизации и регистрации */
public class AuthServiceImpl implements AuthService {

    /** Репозиторий с пользователями */
    private final UserRepository userRepository;

    /** Перевод модели для сервиса <-> модель для базы данных */
    private final MapperDomainDataSourceUser mapperDomainDataSourceUser;

    /**
     * Шифровщик (алгоритм хеширования bcrypt)
     * Пароли хранятся в базе данных в зашифрованном виде и их нельзя дешифровать.
     * Введенный пароль шифруется с использованием того же алгоритма,
     * а затем сравнивается с шифрованным паролем в базе данных.
     * Сравнение выполняет метод matches() объекта PasswordEncoder.
     */
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = encoder;
        this.mapperDomainDataSourceUser = new MapperDomainDataSourceUser(passwordEncoder);
    }

    @Override
    public boolean registration(SignUpRequest signUpRequest) {
        // Если пользователя с таким логином еще не существует и логин-пароль корректной длины
        if (userRepository.findByLogin(signUpRequest.getLogin()) == null
                && isLengthLoginPasswordCorrect(signUpRequest)) {
            // то сохраняем в базу данных
            userRepository.save(mapperDomainDataSourceUser.domainToDataSource(signUpRequest));
            return true;
        } else {
            // иначе возвращаем false
            return false;
        }
    }

    @Override
    public UUID authorization(String loginPassword) throws IllegalArgumentException {
        String decodedString = new String(Base64.getDecoder().decode(loginPassword));

        if (decodedString.equals(":")) {
            // если в строке только двоеточие - значит данных для авторизации нет
            return null;
        }

        String login = decodedString.split(":")[0];
        String password = decodedString.split(":")[1];

        UserDataSourceDto userFromRepo = userRepository.findByLogin(login);

        // Если пользователь с таким логином найден
        if (userFromRepo != null) {
            // Сравниваем зашифрованный пароль из репозитория с зашифрованным полученным паролем
            if (passwordEncoder.matches(password, userFromRepo.getPassword())) {
                return userFromRepo.getUuid();
            }
        }

        return null;
    }

    @Override
    public UserServiceDto getUserById(UUID id) {
        Optional<UserDataSourceDto> user = userRepository.findById(id);

        return user.map(userDataSourceDto ->
                mapperDomainDataSourceUser.dataSourceToService(userDataSourceDto,
                        new UserServiceDto())).orElse(null);
    }

    private boolean isLengthLoginPasswordCorrect(SignUpRequest signUpRequest) {
        // логин и пароль от 3 до 20 символов
        int loginLength = signUpRequest.getLogin().length();
        int passwordLength = signUpRequest.getPassword().length();
        return loginLength >= 3 && loginLength <= 20
                && passwordLength >= 3 && passwordLength <= 20;
    }

}
