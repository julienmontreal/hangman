package ca.qc.jmercier.hangman.controller;

import ca.qc.jmercier.hangman.StringUtils;
import ca.qc.jmercier.hangman.dto.Status;
import ca.qc.jmercier.hangman.entities.GameEntity;
import ca.qc.jmercier.hangman.entities.GameRepository;
import ca.qc.jmercier.hangman.exception.AlreadyAnsweredException;
import ca.qc.jmercier.hangman.exception.EndedGameException;
import ca.qc.jmercier.hangman.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@RestController
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
        GameEntity gameEntity = new GameEntity();
        gameEntity.setRemainingAttempt(nbInitialAttempt);
        gameEntity.setStatus(Status.STARTED);
        String secretWord = wordList.get((int)Math.random()*wordList.size());
        gameEntity.setSecretWord(secretWord);
        gameEntity.setCurrentWord(StringUtils.getUnderscoreString(secretWord));
        gameRepository.save(gameEntity);
        return new ResponseEntity<>(gameEntity, HttpStatus.CREATED);
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<GameEntity> play(@PathVariable Integer id, @RequestBody @NotNull String answer){
        GameEntity game = gameRepository.findOne(id);
        if (game == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(isEndedGame(game)){
            throw new EndedGameException("Game is " + game.getStatus().name());
        }

        gameService.processAnswer(game, answer);
        return new ResponseEntity<>(gameRepository.save(game), HttpStatus.OK);
    }

    private boolean isEndedGame(GameEntity game){
        return !Status.STARTED.equals(game.getStatus());
    };


}
