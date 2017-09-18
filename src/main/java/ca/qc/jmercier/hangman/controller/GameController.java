package ca.qc.jmercier.hangman.controller;

import ca.qc.jmercier.hangman.persistence.GameEntity;
import ca.qc.jmercier.hangman.service.GameService;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/game")
public class GameController {

    private GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<GameEntity> start() {
        return new ResponseEntity<>(gameService.start(), HttpStatus.CREATED);
    }

    @PostMapping(value = "/{gameId}")
    public ResponseEntity<GameEntity> play(@PathVariable("gameId") Integer gameId,
                                           @RequestBody @NotNull @NotEmpty String answer) {
        return new ResponseEntity<>(gameService.play(gameId, answer), HttpStatus.OK);
    }

}
