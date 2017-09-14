package ca.qc.jmercier.hangman.service;

import ca.qc.jmercier.hangman.util.StringUtils;
import ca.qc.jmercier.hangman.entities.Status;
import ca.qc.jmercier.hangman.entities.GameEntity;
import ca.qc.jmercier.hangman.entities.GameRepository;
import ca.qc.jmercier.hangman.exception.AlreadyAnsweredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GameService {

	@Autowired
	private GameRepository gameRepository;

	public void processAnswer(GameEntity game, String answer) {
		game.getAnswers().add(answer);

		try {
			if (isSecretFound(game.getSecretWord(), answer)) {
				game.setStatus(Status.WON);
				game.setCurrentWord(answer);
			} else if (isLetter(answer)) {
				processLetter(game, answer.charAt(0));
			} else { // non winning word
				game.decrementRemainingAttempt();
			}
		} finally {
			gameRepository.save(game);
		}
	}

	private boolean isLetter(String answer) {
		return answer.length() == 1;
	}

	private boolean isSecretFound(String secret, String answer){
		return secret.equalsIgnoreCase(answer);
	}

	private void processLetter(GameEntity game, char letter) {
		validateLetter(game, letter);
		if (containsLetter(game.getSecretWord(), letter)) {
			game.setCurrentWord(StringUtils.replaceLetter(game.getSecretWord(), game.getCurrentWord(), letter));
		} else {
			game.decrementRemainingAttempt();
		}
	}

	private void validateLetter(GameEntity game, char letter) {
		int occurrences = Collections.frequency(game.getAnswers(), letter);
		if (occurrences == 1) {
			throw new AlreadyAnsweredException("Already answered " + letter);
		}
		if (occurrences > 1) {
			game.decrementRemainingAttempt();
			throw new AlreadyAnsweredException("Already answered " + letter + ". Your number of attempt has been decreased by 1.");
		}
	}

	private boolean containsLetter(String word, char letter){
		return word.indexOf(letter) != -1;
	}


}
