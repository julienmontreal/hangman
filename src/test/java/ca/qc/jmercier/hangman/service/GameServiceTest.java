package ca.qc.jmercier.hangman.service;

import ca.qc.jmercier.hangman.exception.AlreadyAnsweredException;
import ca.qc.jmercier.hangman.fixtures.GameEntityFixture;
import ca.qc.jmercier.hangman.persistence.GameEntity;
import ca.qc.jmercier.hangman.persistence.GameRepository;
import ca.qc.jmercier.hangman.persistence.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class GameServiceTest {

	@InjectMocks
	private GameService gameService;

	@Mock
	private GameRepository gameRepository;

	@Captor
	private ArgumentCaptor<GameEntity> gameEntityArgumentCaptor;

	@Test
	public void whenAnwerIsSecretWord_thenStatusIsWon(){
		String secretWord ="secret";
		Integer remainingAttempt = 10;
		GameEntity game = GameEntityFixture.get(Status.STARTED, secretWord, "______", remainingAttempt);
		String answer = "secret";

		gameService.processAnswer(game, answer);

		verify(gameRepository).save(gameEntityArgumentCaptor.capture());
		GameEntity captorValue = gameEntityArgumentCaptor.getValue();
		assertNotNull(captorValue);
		assertTrue(captorValue.getAnswers().contains(answer));
		assertEquals(Status.WON,captorValue.getStatus());
		assertEquals(game.getRemainingAttempt(),captorValue.getRemainingAttempt());
		assertEquals(secretWord, captorValue.getCurrentWord());
	}

	@Test
	public void whenAnwerIsWrongWord_thenRemaingAttemptDecreaseByOne(){
		String secretWord = "secret";
		Integer remainingAttempt = 10;
		String currentWord = "______";
		GameEntity game = GameEntityFixture.get(Status.STARTED, secretWord, currentWord, remainingAttempt);
		String answer = "badAnswer";

		gameService.processAnswer(game, answer);

		verify(gameRepository).save(gameEntityArgumentCaptor.capture());
		GameEntity captorValue = gameEntityArgumentCaptor.getValue();
		assertNotNull(captorValue);
		assertTrue(captorValue.getAnswers().contains(answer));
		assertEquals(Status.STARTED,captorValue.getStatus());
		assertEquals(remainingAttempt-1,captorValue.getRemainingAttempt().intValue());
		assertEquals(currentWord, captorValue.getCurrentWord());
	}

	@Test
	public void whenAnwerIsCorrectLetter_thenCurrentWordIsOk(){
		String secretWord = "secret";
		Integer remainingAttempt = 10;
		String currentWord = "______";
		GameEntity game = GameEntityFixture.get(Status.STARTED, secretWord, currentWord, remainingAttempt);
		String answer = "t";

		gameService.processAnswer(game, answer);

		verify(gameRepository).save(gameEntityArgumentCaptor.capture());
		GameEntity captorValue = gameEntityArgumentCaptor.getValue();
		assertNotNull(captorValue);
		assertTrue(captorValue.getAnswers().contains(answer));
		assertEquals(Status.STARTED,captorValue.getStatus());
		assertEquals(remainingAttempt,captorValue.getRemainingAttempt());
		assertEquals("_____t", captorValue.getCurrentWord());
	}

	@Test(expected = AlreadyAnsweredException.class)
	public void whenAnwerIsAlreadyAnswered_thenExceptionRaised(){
		String secretWord = "secret";
		Integer remainingAttempt = 10;
		String currentWord = "______";
		GameEntity game = GameEntityFixture.get(Status.STARTED, secretWord, currentWord, remainingAttempt);
		String answer = "t";
		game.getAnswers().add(answer);

		gameService.processAnswer(game, answer);
	}

	@Test(expected = AlreadyAnsweredException.class)
	public void whenAnwerIsAlreadyAnsweredTwice_thenExceptionRaised(){
		String secretWord = "secret";
		Integer remainingAttempt = 10;
		String currentWord = "______";
		GameEntity game = GameEntityFixture.get(Status.STARTED, secretWord, currentWord, remainingAttempt);
		String answer = "t";
		game.getAnswers().addAll(Arrays.asList(answer, answer));

		gameService.processAnswer(game, answer);
	}

	@Test
	public void whenRemainingAttemptIsOneAndBadAnswer_thenGameStatusLost(){
		String secretWord = "secret";
		Integer remainingAttempt = 1;
		String currentWord = "______";
		GameEntity game = GameEntityFixture.get(Status.STARTED, secretWord, currentWord, remainingAttempt);
		String answer = "x";

		gameService.processAnswer(game, answer);

		verify(gameRepository).save(gameEntityArgumentCaptor.capture());

		GameEntity captorValue = gameEntityArgumentCaptor.getValue();
		assertNotNull(captorValue);
		assertTrue(captorValue.getAnswers().contains(answer));
		assertEquals(Status.LOST,captorValue.getStatus());
		assertEquals(new Integer(0),captorValue.getRemainingAttempt());
		assertEquals("______", captorValue.getCurrentWord());
	}

}
