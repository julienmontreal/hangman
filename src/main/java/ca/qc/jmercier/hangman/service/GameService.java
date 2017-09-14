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
			if (isLetter(answer)) {
				processAnswerAsLetter(game, answer.charAt(0));
			} else if (game.getSecretWord().equals(answer)) {
				game.setStatus(Status.WON);
				game.setCurrentWord(answer);
			} else {
				game.decrementRemainingAttempt();
			}
		} finally {
			gameRepository.save(game);
		}
	}

	private boolean isLetter(String answer) {
		return answer.length() == 1;
	}

	private void processAnswerAsLetter(GameEntity game, char letter) {
		validateAnswer(game, letter);
		if (game.getSecretWord().indexOf(letter) != -1) {
			game.setCurrentWord(StringUtils.replaceLetter(game.getSecretWord(), game.getCurrentWord(), letter));
		} else {
			game.decrementRemainingAttempt();
		}
	}

	private void validateAnswer(GameEntity game, char letter) {
		int occurrences = Collections.frequency(game.getAnswers(), letter);
		if (occurrences == 1) {
			throw new AlreadyAnsweredException("Already answered " + letter);
		}
		if (occurrences > 1) {
			game.decrementRemainingAttempt();
			throw new AlreadyAnsweredException("Already answered " + letter + ". Your number of attempt has been decreased by 1.");
		}
	}


}
