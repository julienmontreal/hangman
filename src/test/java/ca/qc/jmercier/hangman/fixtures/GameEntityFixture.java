package ca.qc.jmercier.hangman.fixtures;

import ca.qc.jmercier.hangman.persistence.GameEntity;
import ca.qc.jmercier.hangman.persistence.Status;

public class GameEntityFixture {

	public static GameEntity get(Status status, String secretWord, String currentWord, Integer remainingAttempt){
		return new GameEntity(status, secretWord, currentWord, remainingAttempt);
	}
}
