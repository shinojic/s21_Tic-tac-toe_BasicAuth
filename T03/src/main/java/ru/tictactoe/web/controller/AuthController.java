package ru.tictactoe.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tictactoe.domain.model.SignUpRequest;
import ru.tictactoe.domain.service.AuthService;

@Controller
@RequestMapping("/tic-tac-toe")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/registration")
    public String registerForm() {
        return "registration";
    }

    @PostMapping("/registration")
    public String processRegistration(@RequestParam("login") String login,
                                      @RequestParam("password") String password) {
        SignUpRequest signUpRequest = new SignUpRequest(login, password);
        if (authService.registration(signUpRequest)) {
            return "redirect:/tic-tac-toe/auth";
        } else {
            return  "error";  // некорректный логин или пароль
        }
    }

    @GetMapping("/auth")
    public String authForm() {
        return "auth";
    }

    @PostMapping("/log")
    public String authProcess() {
        return "home";
    }

}
