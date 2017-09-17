package ca.qc.jmercier.hangman.controller;

import ca.qc.jmercier.hangman.exception.EndedGameException;
import ca.qc.jmercier.hangman.persistence.GameEntity;
import ca.qc.jmercier.hangman.persistence.GameRepository;
import ca.qc.jmercier.hangman.persistence.Status;
import ca.qc.jmercier.hangman.service.GameService;
import ca.qc.jmercier.hangman.util.RandomWordHelper;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

import static ca.qc.jmercier.hangman.util.StringUtils.getUnderscoreString;

@RestController
@RequestMapping("/game")
public class GameController {

    private static Logger log = LoggerFactory.getLogger(GameController.class);

    private GameRepository gameRepository;

    private GameService gameService;

    private RandomWordHelper helper;

    private final Integer nbInitialAttempt;

    @Autowired
    public GameController(GameRepository gameRepository,
                          GameService gameService,
                          RandomWordHelper helper,
                          @Value("${hangman.nbInitialAttempt:10}") Integer nbInitialAttempt) {
        this.gameRepository = gameRepository;
        this.gameService = gameService;
        this.helper = helper;
        this.nbInitialAttempt = nbInitialAttempt;
    }

    @GetMapping
    public ResponseEntity<GameEntity> start() {
        log.info("Starting a new game");
        String secretWord = helper.getRandomWord();
        String currentWord = getUnderscoreString(secretWord);
        GameEntity gameEntity =
            new GameEntity(Status.STARTED, secretWord, currentWord, nbInitialAttempt);
        return new ResponseEntity<>(gameRepository.save(gameEntity), HttpStatus.CREATED);
    }

    @PostMapping(value = "/{gameId}")
    public ResponseEntity<GameEntity> play(@PathVariable("gameId") Integer gameId,
                                           @RequestBody @NotNull @NotEmpty String answer) {

        log.info(String.format("Playing game: [%s] with answer: [%s]" , gameId , answer));

        GameEntity game = gameRepository.findOne(gameId);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (game.isEnded()) {
            throw new EndedGameException("Game is " + game.getStatus().name());
        }

        return new ResponseEntity<>(gameService.processAnswer(game, answer), HttpStatus.OK);
    }

}
