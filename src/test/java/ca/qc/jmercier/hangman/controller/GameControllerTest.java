package ca.qc.jmercier.hangman.controller;

import ca.qc.jmercier.hangman.persistence.GameEntity;
import ca.qc.jmercier.hangman.persistence.Status;
import ca.qc.jmercier.hangman.service.GameService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class GameControllerTest {

    @InjectMocks
    private GameController gameController;

    @Mock
    private GameService gameService;

    @Test
    public void whenStart_thenOk(){
        GameEntity game1 = new GameEntity(Status.STARTED, "secret", "______", 10);
        when(gameService.start()).thenReturn(game1);
        ResponseEntity<GameEntity> newGame = gameController.start();
        assertEquals(Status.STARTED, newGame.getBody().getStatus());
    }

    @Test
    public void whenPlay_thenReturnOk(){

        GameEntity game1 = new GameEntity(Status.STARTED, "", "", 0);
        when(gameService.play(1, "a")).thenReturn(game1);

        ResponseEntity response = gameController.play(1,"a");

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }
}
