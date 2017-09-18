package ca.qc.jmercier.hangman.service;

import ca.qc.jmercier.hangman.exception.AlreadyAnsweredException;
import ca.qc.jmercier.hangman.exception.AlreadyAnsweredTwiceException;
import ca.qc.jmercier.hangman.fixtures.GameEntityFixture;
import ca.qc.jmercier.hangman.persistence.GameEntity;
import ca.qc.jmercier.hangman.persistence.GameRepository;
import ca.qc.jmercier.hangman.persistence.Status;
import org.junit.Before;
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
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class GameServiceTest {

	@InjectMocks
	private GameService gameService;

	@Mock
	private GameRepository gameRepository;

	@Captor
	private ArgumentCaptor<GameEntity> gameEntityArgumentCaptor;

	private GameEntity game1;

	@Before
	public void setUp(){
		game1 = GameEntityFixture.get(Status.STARTED, "secret", "______", 10);
		when(gameRepository.findOne(1)).thenReturn(game1);
	}

	@Test
	public void whenAnwerIsSecretWord_thenStatusIsWon(){
		Integer gameId = 1;
		String answer = "secret";

		gameService.play(gameId, answer);

		verify(gameRepository).save(gameEntityArgumentCaptor.capture());

		GameEntity game = gameEntityArgumentCaptor.getValue();
		assertNotNull(game);
		assertTrue(game.getAnswers().contains(answer));
		assertEquals(Status.WON,game.getStatus());
		assertEquals(game1.getRemainingAttempt(),game.getRemainingAttempt());
		assertEquals(game1.getSecretWord(), game.getCurrentWord());
	}

	@Test
	public void whenAnwerIsWrongWord_thenRemaingAttemptDecreaseByOne(){
		Integer gameId = 1;
		String answer = "badAnswer";

		gameService.play(gameId, answer);

		verify(gameRepository).save(gameEntityArgumentCaptor.capture());
		GameEntity captorValue = gameEntityArgumentCaptor.getValue();
		assertNotNull(captorValue);
		assertTrue(captorValue.getAnswers().contains(answer));
		assertEquals(Status.STARTED,captorValue.getStatus());
		assertEquals(game1.getRemainingAttempt(),captorValue.getRemainingAttempt());
		assertEquals(game1.getCurrentWord(), captorValue.getCurrentWord());
	}

	@Test
	public void whenAnwerIsCorrectLetter_thenCurrentWordIsOk(){
		Integer gameId = 1;
		String answer = "t";

		gameService.play(gameId, answer);

		verify(gameRepository).save(gameEntityArgumentCaptor.capture());
		GameEntity captorValue = gameEntityArgumentCaptor.getValue();
		assertNotNull(captorValue);
		assertTrue(captorValue.getAnswers().contains(answer));
		assertEquals(Status.STARTED,captorValue.getStatus());
		assertEquals(game1.getRemainingAttempt(),captorValue.getRemainingAttempt());
		assertEquals("_____t", captorValue.getCurrentWord());
	}

	@Test(expected = AlreadyAnsweredException.class)
	public void whenAnwerIsAlreadyAnswered_thenExceptionRaised(){
		Integer gameId = 1;
		String answer = "t";
		game1.getAnswers().add(answer);

		gameService.play(gameId, answer);
	}

	@Test(expected = AlreadyAnsweredTwiceException.class)
	public void whenAnwerIsAlreadyAnsweredTwice_thenExceptionRaised(){
		Integer gameId = 1;
		String answer = "t";
		game1.getAnswers().addAll(Arrays.asList(answer, answer));

		gameService.play(gameId, answer);
	}

	@Test
	public void whenRemainingAttemptIsOneAndBadAnswer_thenGameStatusLost(){
		Integer gameId = 1;
		String answer = "x";
		game1.setRemainingAttempt(1);

		gameService.play(gameId, answer);

		verify(gameRepository).save(gameEntityArgumentCaptor.capture());

		GameEntity captorValue = gameEntityArgumentCaptor.getValue();
		assertNotNull(captorValue);
		assertTrue(captorValue.getAnswers().contains(answer));
		assertEquals(Status.LOST,captorValue.getStatus());
		assertEquals(new Integer(0),captorValue.getRemainingAttempt());
		assertEquals("______", captorValue.getCurrentWord());
	}

}
