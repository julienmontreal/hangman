package ca.qc.jmercier.hangman.controller;

import ca.qc.jmercier.hangman.util.StringUtils;
import ca.qc.jmercier.hangman.entities.Status;
import ca.qc.jmercier.hangman.entities.GameEntity;
import ca.qc.jmercier.hangman.entities.GameRepository;
import ca.qc.jmercier.hangman.exception.EndedGameException;
import ca.qc.jmercier.hangman.service.GameService;
import org.hibernate.validator.constraints.NotEmpty;
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
import java.util.List;

@RestController
@RequestMapping("/game2")
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameService gameService;

    @Value("${hangman.nbInitialAttempt:10}")
    private Integer nbInitialAttempt;

    @Value("#{'${hangman.wordList}'.split(',')}")
    private List<String> wordList;

    @GetMapping
    public ResponseEntity<GameEntity> start(){
        String secretWord = wordList.get((int)(Math.random()*wordList.size()));
        GameEntity gameEntity = new GameEntity(Status.STARTED, secretWord, StringUtils.getUnderscoreString(secretWord), nbInitialAttempt);
        gameRepository.save(gameEntity);
        return new ResponseEntity<>(gameEntity, HttpStatus.CREATED);
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<GameEntity> play(@PathVariable Integer id, @RequestBody @NotNull @NotEmpty String answer){
        GameEntity game = gameRepository.findOne(id);
        if (game == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(isEndedGame(game)){
            throw new EndedGameException("Game is " + game.getStatus().name());
        }

        gameService.processAnswer(game, answer);
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    private boolean isEndedGame(GameEntity game){
        return !Status.STARTED.equals(game.getStatus());
    };
}
