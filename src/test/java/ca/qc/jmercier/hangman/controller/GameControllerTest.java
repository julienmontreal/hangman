package ca.qc.jmercier.hangman.controller;

import ca.qc.jmercier.hangman.exception.EndedGameException;
import ca.qc.jmercier.hangman.persistence.GameEntity;
import ca.qc.jmercier.hangman.persistence.GameRepository;
import ca.qc.jmercier.hangman.persistence.Status;
import ca.qc.jmercier.hangman.service.GameService;
import ca.qc.jmercier.hangman.util.RandomWordHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class GameControllerTest {

    @InjectMocks
    private GameController gameController;

    @Mock
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private RandomWordHelper helper;

    @Before
    public void setUp(){
    }

    @Test
    public void whenStart_thenOk(){
        GameEntity game1 = new GameEntity(Status.STARTED, "secret", "______", 10);
        when(gameRepository.save(any(GameEntity.class))).thenReturn(game1);
        when(helper.getRandomWord()).thenReturn("secret");
        ResponseEntity<GameEntity> newGame = gameController.start();
        assertEquals(Status.STARTED, newGame.getBody().getStatus());
    }

    @Test(expected = EndedGameException.class)
    public void whenPlayAndGameEnded_thenEndedGameException(){
        GameEntity game1 = new GameEntity(Status.LOST, "", "", 0);
        when(gameRepository.findOne(1)).thenReturn(game1);

        gameController.play(1,"a");
    }

    @Test
    public void whenPlay_thenReturnOk(){
        GameEntity game1 = new GameEntity(Status.STARTED, "", "", 0);
        when(gameRepository.findOne(1)).thenReturn(game1);

        ResponseEntity response = gameController.play(1,"a");

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }


    @Test
    public void whenGameDoesNotExist_thenStatusCode404(){
        when(gameRepository.findOne(1)).thenReturn(null);

        ResponseEntity response = gameController.play(1,"a");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
