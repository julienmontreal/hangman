package ca.qc.jmercier.hangman.service;

import ca.qc.jmercier.hangman.entities.GameEntity;
import ca.qc.jmercier.hangman.entities.GameRepository;
import ca.qc.jmercier.hangman.entities.Status;
import ca.qc.jmercier.hangman.exception.AlreadyAnsweredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static ca.qc.jmercier.hangman.util.StringUtils.replaceLetter;

@Service
public class GameService {

	@Autowired
	private GameRepository gameRepository;

	public void processAnswer(GameEntity game, String answer) {
		game.getAnswers().add(answer);

		try {
			validateAnswer(game, answer);

			if (isCorrectAnswer(game.getSecretWord(),answer)) {
				game.setCurrentWord(replaceAnswer(game.getSecretWord(), game.getCurrentWord(), answer));
				if(game.isSecretWordFound()){
					game.setStatus(Status.WON);
				}
			} else { // bad answer
				game.decrementRemainingAttempt();
			}
		} finally {
			gameRepository.save(game);
		}
	}

	private String replaceAnswer(String secretWord, String currentWord, String answer){
		if (secretWord.equalsIgnoreCase(answer)){
			return answer;
		}
		assert answer.length() == 1;
		return replaceLetter(secretWord, currentWord, answer.charAt(0));
	}

	private boolean isCorrectAnswer(String secretWord, String answer){
		return secretWord.equalsIgnoreCase(answer) || (answer.length() == 1 && secretWord.indexOf(answer) != -1);
	}

	private void validateAnswer(GameEntity game, String answer) {
		int occurrences = Collections.frequency(game.getAnswers(), answer);
		if (occurrences == 1) {
			throw new AlreadyAnsweredException("Already answered " + answer);
		}
		if (occurrences > 1) {
			game.decrementRemainingAttempt();
			throw new AlreadyAnsweredException("Already answered " + answer + ". Your number of attempt has been decreased by 1.");
		}
	}

}
