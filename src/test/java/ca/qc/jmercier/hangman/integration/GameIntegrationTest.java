package ca.qc.jmercier.hangman.integration;

import ca.qc.jmercier.hangman.entities.GameEntity;
import ca.qc.jmercier.hangman.entities.GameRepository;
import ca.qc.jmercier.hangman.entities.Status;
import ca.qc.jmercier.hangman.fixtures.GameEntityFixture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GameIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private GameRepository gameRepository;

	@Before
	public void initBD(){
		GameEntity game = GameEntityFixture.get(Status.STARTED,"secret", "______",10);
		gameRepository.save(game);
	}

	@Test
	public void whenStartNewGame_thenOk() {
		ResponseEntity<GameEntity> responseEntity =
				restTemplate.getForEntity("/game", GameEntity.class);
		GameEntity gameEntity = responseEntity.getBody();
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		assertNotNull(gameEntity.getId());
		assertNotNull(gameEntity.getCurrentWord());
		assertNull(gameEntity.getSecretWord());
		assertEquals(Collections.EMPTY_LIST, gameEntity.getAnswers());
		assertEquals(Status.STARTED, gameEntity.getStatus());
		assertEquals(new Integer(10), gameEntity.getRemainingAttempt());
	}

	@Test
	public void whenGameIdExist_thenOk() {
		ResponseEntity<GameEntity> responseEntity =
				restTemplate.getForEntity("/game/1", GameEntity.class);
		GameEntity gameEntity = responseEntity.getBody();
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertNotNull(gameEntity.getId());
		assertNotNull(gameEntity.getCurrentWord());
		assertNull(gameEntity.getSecretWord());
		assertThat(gameEntity.getAnswers()).isEqualTo(Collections.EMPTY_LIST);
		assertEquals(Status.STARTED, gameEntity.getStatus());
		assertEquals(new Integer(10), gameEntity.getRemainingAttempt());
	}

	@Test
	public void whenGameIdDoesNotExist_thenReturns404() {
		ResponseEntity<GameEntity> responseEntity =
				restTemplate.getForEntity("/game/100", GameEntity.class);
		GameEntity gameEntity = responseEntity.getBody();
		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	}

	@Test
	public void whenPlayingCorrectLetter_thenOk() {
		String answer = "t";
		ResponseEntity<GameEntity> responseEntity =
				restTemplate.postForEntity("/game/1",new HttpEntity<>(answer), GameEntity.class);
		GameEntity gameEntity = responseEntity.getBody();
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(new Integer(1),gameEntity.getId());
		assertEquals("_____t", gameEntity.getCurrentWord());
		assertNull(gameEntity.getSecretWord());
		assertEquals(Collections.singletonList(answer), gameEntity.getAnswers());
		assertEquals(Status.STARTED, gameEntity.getStatus());
		assertEquals(new Integer(10), gameEntity.getRemainingAttempt());
	}

	@Test
	public void whenPlayingLetterInDouble_thenOk() {
		String answer = "e";
		ResponseEntity<GameEntity> responseEntity =
				restTemplate.postForEntity("/game/1",new HttpEntity<>(answer), GameEntity.class);
		GameEntity gameEntity = responseEntity.getBody();
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(new Integer(1),gameEntity.getId());
		assertEquals("_e__e_", gameEntity.getCurrentWord());
		assertNull(gameEntity.getSecretWord());
		assertEquals(Collections.singletonList(answer), gameEntity.getAnswers());
		assertEquals(Status.STARTED, gameEntity.getStatus());
		assertEquals(new Integer(10), gameEntity.getRemainingAttempt());
	}

	@Test
	public void whenPlayingWrongLetter_thenRemainingAttemptMinusOne() {
		String answer = "w";
		ResponseEntity<GameEntity> responseEntity =
				restTemplate.postForEntity("/game/1",new HttpEntity<>(answer), GameEntity.class);
		GameEntity gameEntity = responseEntity.getBody();
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(new Integer(1),gameEntity.getId());
		assertEquals("______", gameEntity.getCurrentWord());
		assertNull(gameEntity.getSecretWord());
		assertEquals(Collections.singletonList(answer), gameEntity.getAnswers());
		assertEquals(Status.STARTED, gameEntity.getStatus());
		assertEquals(new Integer(9), gameEntity.getRemainingAttempt());
	}

	@Test
	public void whenPlayingWrongAnswer_thenRemainingAttemptMinusOne() {
		String answer = "badAnswer";
		ResponseEntity<GameEntity> responseEntity =
				restTemplate.postForEntity("/game/1",new HttpEntity<>(answer), GameEntity.class);
		GameEntity gameEntity = responseEntity.getBody();
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(new Integer(1),gameEntity.getId());
		assertEquals("______", gameEntity.getCurrentWord());
		assertNull(gameEntity.getSecretWord());
		assertEquals(Collections.singletonList(answer), gameEntity.getAnswers());
		assertEquals(Status.STARTED, gameEntity.getStatus());
		assertEquals(new Integer(9), gameEntity.getRemainingAttempt());
	}

	@Test
	public void whenPlayingGoodAnswer_thenOk() {
		String answer = "secret";
		ResponseEntity<GameEntity> responseEntity =
				restTemplate.postForEntity("/game/1",new HttpEntity<>(answer), GameEntity.class);
		GameEntity gameEntity = responseEntity.getBody();
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(new Integer(1),gameEntity.getId());
		assertEquals("secret", gameEntity.getCurrentWord());
		assertNull(gameEntity.getSecretWord());
		assertEquals(Collections.singletonList(answer), gameEntity.getAnswers());
		assertEquals(Status.WON, gameEntity.getStatus());
		assertEquals(new Integer(10), gameEntity.getRemainingAttempt());
	}

	@Test
	public void whenPlayingTwiceSameAnswer_thenBadRequest() {
		String answer = "e";
		restTemplate.postForObject("/game/1",new HttpEntity<>(answer), Object.class);
		restTemplate.postForObject("/game/1",new HttpEntity<>(answer), Object.class);
		GameEntity gameEntity = gameRepository.findOne(1);
		assertEquals(new Integer(1),gameEntity.getId());
		assertEquals("_e__e_", gameEntity.getCurrentWord());
		assertEquals(Arrays.asList(answer, answer), gameEntity.getAnswers());
		assertEquals(Status.STARTED, gameEntity.getStatus());
		assertEquals(new Integer(10), gameEntity.getRemainingAttempt());
	}

	@Test
	public void whenPlayingThreeTimesSameAnswer_thenBadRequestAndRemaingAttemptDecreases() {
		String answer = "e";
		restTemplate.postForObject("/game/1",new HttpEntity<>(answer), Object.class);
		restTemplate.postForObject("/game/1",new HttpEntity<>(answer), Object.class);
		restTemplate.postForObject("/game/1",new HttpEntity<>(answer), Object.class);
		GameEntity gameEntity = gameRepository.findOne(1);
		assertEquals(new Integer(1),gameEntity.getId());
		assertEquals("_e__e_", gameEntity.getCurrentWord());
		assertEquals(Arrays.asList(answer, answer, answer), gameEntity.getAnswers());
		assertEquals(Status.STARTED, gameEntity.getStatus());
		assertEquals(new Integer(9), gameEntity.getRemainingAttempt());
	}

	@Test
	public void whenPlayingTwiceSameWrongAnswer_thenBadRequest() {
		String answer = "z";
		restTemplate.postForObject("/game/1",new HttpEntity<>(answer), Object.class);
		restTemplate.postForObject("/game/1",new HttpEntity<>(answer), Object.class);
		GameEntity gameEntity = gameRepository.findOne(1);
		assertEquals(new Integer(1),gameEntity.getId());
		assertEquals("______", gameEntity.getCurrentWord());
		assertEquals(Arrays.asList(answer, answer), gameEntity.getAnswers());
		assertEquals(Status.STARTED, gameEntity.getStatus());
		assertEquals(new Integer(9), gameEntity.getRemainingAttempt());
	}

	@Test
	public void whenPlayingThreeTimesSameWrongAnswer_thenBadRequestAndRemaingAttemptDecreases() {
		String answer = "z";
		restTemplate.postForObject("/game/1",new HttpEntity<>(answer), Object.class);
		restTemplate.postForObject("/game/1",new HttpEntity<>(answer), Object.class);
		restTemplate.postForObject("/game/1",new HttpEntity<>(answer), Object.class);
		GameEntity gameEntity = gameRepository.findOne(1);
		assertEquals(new Integer(1),gameEntity.getId());
		assertEquals("______", gameEntity.getCurrentWord());
		assertEquals(Arrays.asList(answer, answer, answer), gameEntity.getAnswers());
		assertEquals(Status.STARTED, gameEntity.getStatus());
		assertEquals(new Integer(8), gameEntity.getRemainingAttempt());
	}
}
