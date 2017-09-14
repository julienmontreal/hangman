package ca.qc.jmercier.hangman.fixtures;

import ca.qc.jmercier.hangman.entities.GameEntity;
import ca.qc.jmercier.hangman.entities.Status;

public class GameEntityFixture {

	public static GameEntity get(){
		return new GameEntity(Status.STARTED, "secret", "______", 10);
	}
}
