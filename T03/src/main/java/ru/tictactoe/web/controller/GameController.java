package ru.tictactoe.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tictactoe.domain.model.GameServiceDto;
import ru.tictactoe.domain.model.GameState;
import ru.tictactoe.domain.model.UserServiceDto;
import ru.tictactoe.domain.service.AuthService;
import ru.tictactoe.domain.service.GameService;
import ru.tictactoe.web.mapper.MapperDomainWeb;
import ru.tictactoe.web.model.GameWebDto;

import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping("/tic-tac-toe")
public class GameController {

    private final GameService gameService;
    private final AuthService userService;
    private final MapperDomainWeb mapperDomainWeb;

    @Autowired
    public GameController(GameService gameService, AuthService userService) {
        this.gameService = gameService;
        this.userService = userService;
        this.mapperDomainWeb = new MapperDomainWeb();
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/profile")
    public String seeProfile(@AuthenticationPrincipal String userId, Model model) {
        model.addAttribute("profile_id", userService.getUserById(UUID.fromString(userId)).getId());
        model.addAttribute("profile_login", userService.getUserById(UUID.fromString(userId)).getLogin());
        return "profile";
    }

    @GetMapping("/sessions")
    public String getSessions(@AuthenticationPrincipal String userId, Model model) {
        model.addAttribute("allUuid", gameService.getAllGames(userId));
        return "sessions";
    }

    @GetMapping("/waiting-sessions")
    public String getWaitingSessions(@AuthenticationPrincipal String userId, Model model) {
        model.addAttribute("waitingUuid", gameService.getWaitingGames(userId));
        return "waiting-sessions";
    }

    @PostMapping("/waiting-sessions")
    public String joinGame(@AuthenticationPrincipal String userId, @RequestParam UUID gameId) {
        UserServiceDto user = userService.getUserById(UUID.fromString(userId));
        gameService.joinGame(gameService.getGameById(gameId), user);
        return "redirect:/tic-tac-toe/game/" + gameId;
    }

    @PostMapping("/start")
    public String startGame(@AuthenticationPrincipal String userId) {
        UserServiceDto user = userService.getUserById(UUID.fromString(userId));
        GameServiceDto newGame = gameService.setupGame(new GameServiceDto(), user);
        return "redirect:/tic-tac-toe/game/" + newGame.getUuid();
    }

    @PostMapping("/start-computer")
    public String startGameComputer() {
        GameServiceDto newTicTacToe = gameService.setupGameWithComputer(new GameServiceDto());
        return "redirect:/tic-tac-toe/game-computer/" + newTicTacToe.getUuid();
    }

    @GetMapping("/game/{uuid}")
    public String getGame(@PathVariable("uuid") String uuid, Model model) {

        GameServiceDto ticTacToe = gameService.getGameById(UUID.fromString(uuid));

        if (ticTacToe == null) {
            System.out.println("UUID is not found");
            return "UUID is not found";
        }

        model.addAttribute("playingField", mapperDomainWeb.
                domainToWeb(ticTacToe, new GameWebDto()).getPlayingField().getMatrix());
        model.addAttribute("uuid", ticTacToe.getUuid());
        model.addAttribute("playerOne", ticTacToe.getPlayerOne().getLogin());
        if (ticTacToe.getPlayerTwo() != null) {
            model.addAttribute("playerTwo", ticTacToe.getPlayerTwo().getLogin());
        } else {
            model.addAttribute("playerTwo", "ожидается");
        }
        model.addAttribute("status", ticTacToe.getState().toString());

        return "game";
    }

    /**
     * Ход игрока в игре с другим игроком
     * @param uuid Идентификатор игры
     * @param i индекс строки
     * @param j индекс столбца
     * @return game.html
     */
    @PostMapping("/game/{uuid}")
    public String playGame(@AuthenticationPrincipal String userId, @PathVariable("uuid") String uuid,
                                   @RequestParam("row") int i, @RequestParam("col") int j) {
        GameServiceDto ticTacToe = gameService.getGameById(UUID.fromString(uuid));

        if (ticTacToe == null) {
            System.out.println("UUID is not found");
            return "UUID is not found";
        }

        if (gameService.isValidGame(ticTacToe) &&
                (ticTacToe.getState() == GameState.PLAYER1_TURN || ticTacToe.getState() == GameState.PLAYER2_TURN)) {

            gameService.playerTurn(UUID.fromString(userId), ticTacToe, i, j);

        } else if (!gameService.isValidGame(ticTacToe)) {
            System.out.println("Invalid game");
            return "Invalid game";
        }

        return "redirect:/tic-tac-toe/game/" + ticTacToe.getUuid();
    }

    /**
     * Отображение текущего состояния игры
     * @param uuid game id
     * @param model game (id, playing field, status)
     * @return game-computer.html
     */
    @GetMapping("/game-computer/{uuid}")
    public String getGameComputer(@PathVariable("uuid") String uuid, Model model) {

        GameServiceDto ticTacToe = gameService.getGameById(UUID.fromString(uuid));

        if (ticTacToe == null) {
            System.out.println("UUID is not found");
            return "UUID is not found";
        }

        model.addAttribute("playingField", mapperDomainWeb.
                    domainToWeb(ticTacToe, new GameWebDto()).getPlayingField().getMatrix());
        model.addAttribute("uuid", ticTacToe.getUuid());
        model.addAttribute("status", gameService.gameStatus(ticTacToe));

        return "game-computer";
    }

    /**
     * Ход игрока против компьютера
     * @param uuid game uuid
     * @param i индекс строки
     * @param j индекс столбца
     * @return game-computer.html
     */
    @PostMapping("/game-computer/{uuid}")
    public String playGameComputer(@PathVariable("uuid") String uuid,
                               @RequestParam("row") int i, @RequestParam("col") int j) {
        GameServiceDto ticTacToe = gameService.getGameById(UUID.fromString(uuid));

        if (ticTacToe == null) {
            System.out.println("UUID is not found");
            return "UUID is not found";
        }

        if (gameService.isValidGame(ticTacToe) && Objects.equals(gameService.gameStatus(ticTacToe), "CONTINUE")) {
            if (gameService.playerTurnWithComputer(ticTacToe, i, j)) {
                ticTacToe = gameService.computerTurn(ticTacToe);
            } else {
                return "redirect:/tic-tac-toe/game-computer/" + ticTacToe.getUuid();
            }
        } else if (!gameService.isValidGame(ticTacToe)) {
            System.out.println("Invalid game");
            return "Invalid game";
        }

        return "redirect:/tic-tac-toe/game-computer/" + ticTacToe.getUuid();
    }

}
