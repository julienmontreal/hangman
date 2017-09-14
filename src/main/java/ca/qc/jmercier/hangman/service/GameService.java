package ca.qc.jmercier.hangman.service;

import ca.qc.jmercier.hangman.StringUtils;
import ca.qc.jmercier.hangman.dto.Status;
import ca.qc.jmercier.hangman.entities.GameEntity;
import ca.qc.jmercier.hangman.entities.GameRepository;
import ca.qc.jmercier.hangman.exception.AlreadyAnsweredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
			}
			game.decrementRemainingAttempt();
		} finally {
			gameRepository.save(game);
		}
	}

	private void processAnswerAsLetter(GameEntity game, char letter) {
		int occurrences = Collections.frequency(game.getAnswers(), letter);
		if (occurrences == 1) {
			throw new AlreadyAnsweredException("Already answered " + letter);
		}
		if (occurrences > 1) {
			game.decrementRemainingAttempt();
			throw new AlreadyAnsweredException("Already answered " + letter + ". Your number of attempt has been decreased by 1.");
		}
		game.setCurrentWord(StringUtils.replaceLetter(game.getSecretWord(), game.getCurrentWord(), letter));
	}

	private boolean isLetter(String answer) {
		return answer.length() == 1;
	}

}
