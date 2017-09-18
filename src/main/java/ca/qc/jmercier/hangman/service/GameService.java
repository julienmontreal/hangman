package ca.qc.jmercier.hangman.service;

import ca.qc.jmercier.hangman.exception.AlreadyAnsweredException;
import ca.qc.jmercier.hangman.exception.AlreadyAnsweredTwiceException;
import ca.qc.jmercier.hangman.exception.EndedGameException;
import ca.qc.jmercier.hangman.exception.GameNotFoundException;
import ca.qc.jmercier.hangman.persistence.GameEntity;
import ca.qc.jmercier.hangman.persistence.GameRepository;
import ca.qc.jmercier.hangman.persistence.Status;
import ca.qc.jmercier.hangman.util.RandomWordHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static ca.qc.jmercier.hangman.util.StringUtils.getUnderscoreString;
import static ca.qc.jmercier.hangman.util.StringUtils.replaceLetter;

@Service
public class GameService {

    private GameRepository gameRepository;
    private RandomWordHelper randomWordHelper;
    private final Integer nbInitialAttempt;

    @Autowired
    public GameService(GameRepository gameRepository,
                       RandomWordHelper randomWordHelper,
                       @Value("${hangman.nbInitialAttempt:10}") Integer nbInitialAttempt) {
        this.gameRepository = gameRepository;
        this.randomWordHelper = randomWordHelper;
        this.nbInitialAttempt = nbInitialAttempt;
    }

    public GameEntity start() {
        String secretWord = randomWordHelper.getRandomWord();
        String currentWord = getUnderscoreString(secretWord);
        GameEntity gameEntity = new GameEntity(Status.STARTED, secretWord, currentWord, nbInitialAttempt);
        return gameRepository.save(gameEntity);
    }

    public GameEntity play(Integer gameId, String answer) {
        GameEntity game = gameRepository.findOne(gameId);
        if (game == null) {
            throw new GameNotFoundException("Game with id:" + gameId + " does not exists");
        }

        if (isEnded(game)) {
            throw new EndedGameException("Game is " + game.getStatus().name());
        }
        return processAnswer(game, answer);
    }

    private GameEntity processAnswer(GameEntity game, String answer) {

        try {
            if (answer.length() == 1) {//answer is a letter
                processLetter(game, answer);
            } else { //answer is a word
                processWord(game, answer);
            }
        } finally {
            game.getAnswers().add(answer);
            gameRepository.save(game);
        }
        return game;
    }


    private void processLetter(GameEntity game, String letter){
        validateLetter(game, letter);
        if (game.getSecretWord().contains(letter)) {
            game.setCurrentWord(replaceLetter(game.getSecretWord(), game.getCurrentWord(), letter.charAt(0)));
            if (game.getSecretWord().equalsIgnoreCase(game.getCurrentWord())) {
                game.setStatus(Status.WON);
            }
        } else { // bad letter
            decrementRemainingAttempt(game);
        }
    }

    private void validateLetter(GameEntity game, String answer) {
        int occurrences = Collections.frequency(game.getAnswers(), answer);
        if (occurrences == 1) {
            throw new AlreadyAnsweredException("Already answered " + answer);
        }
        if (occurrences > 1) {
            decrementRemainingAttempt(game);
            throw new AlreadyAnsweredTwiceException(
                "Already answered " + answer + ". Your number of attempt has been decreased by 1.");
        }
    }

    private void processWord(GameEntity game, String answer){
        if (game.getSecretWord().equalsIgnoreCase(answer)) { //Found
            game.setCurrentWord(answer);
            game.setStatus(Status.WON);
        } else { //Not found
            decrementRemainingAttempt(game);
        }
    }

    private void decrementRemainingAttempt(GameEntity game){
        Integer remainingAttempt = game.getRemainingAttempt()-1;
        game.setRemainingAttempt(remainingAttempt);
        if (remainingAttempt == 0){
            game.setStatus(Status.LOST);
        }
    }

    private boolean isEnded(GameEntity game){
        return !Status.STARTED.equals(game.getStatus());
    }

}
