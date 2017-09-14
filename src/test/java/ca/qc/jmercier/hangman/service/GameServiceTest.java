package ca.qc.jmercier.hangman.service;

import ca.qc.jmercier.hangman.entities.GameEntity;
import ca.qc.jmercier.hangman.entities.GameRepository;
import ca.qc.jmercier.hangman.fixtures.GameEntityFixture;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

public class GameServiceTest {

	@InjectMocks
	private GameService gameService;

	@Mock
	private GameRepository gameRepository;

	@Test
	public void processAnswer(){
		GameEntity game = GameEntityFixture.get();

		gameService.processAnswer(game, "badAnswer");


	}
}
