package ca.qc.jmercier.hangman.controller;

import ca.qc.jmercier.hangman.dto.Status;
import ca.qc.jmercier.hangman.entities.GameEntity;
import ca.qc.jmercier.hangman.entities.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController(value = "/game")
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    @GetMapping
    public GameEntity start(){
        GameEntity game = new GameEntity();
        game.setRemainingAttempt(10);
        game.setStatus(Status.STARTED);
        game.setSecretWord("random");
        gameRepository.save(game);
        return game;
    }

    @PostMapping(value = "/{id}")
    public ResponseEntity<GameEntity> play(@PathVariable Integer id, @RequestBody @NotNull String word){
        GameEntity gameEntity = gameRepository.findOne(id);
        if (gameEntity == null){
            return new ResponseEntity<GameEntity>(HttpStatus.NOT_FOUND);
        }

        if (word!=null && word.length()==1){
            gameEntity.getAnswers().add(word);
            return new ResponseEntity<GameEntity>(gameRepository.save(gameEntity), HttpStatus.OK);
        }

        if (gameEntity.getSecretWord().equals(word)){
            gameEntity.setStatus(Status.WON);
            return new ResponseEntity<GameEntity>(gameRepository.save(gameEntity), HttpStatus.OK);
        }

        gameEntity.
    }

}
