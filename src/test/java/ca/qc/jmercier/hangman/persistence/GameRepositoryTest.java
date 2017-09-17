package ca.qc.jmercier.hangman.persistence;

import ca.qc.jmercier.hangman.persistence.GameEntity;
import ca.qc.jmercier.hangman.persistence.GameRepository;
import ca.qc.jmercier.hangman.persistence.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class GameRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository employeeRepository;

    @Test
    public void whenFindOne_thenReturnGameEntity(){
        // given
        GameEntity game = new GameEntity(Status.STARTED, "secret", "_____", 2);
        game.setAnswers(Arrays.asList("a", "b", "1", "response"));
        game = entityManager.persist(game);
        entityManager.flush();

        // when
        GameEntity found = employeeRepository.findOne(1);

        // then
        assertThat(found.getId()).isEqualTo(game.getId());
        assertThat(found.getStatus()).isEqualTo(game.getStatus());
        assertThat(found.getSecretWord()).isEqualTo(game.getSecretWord());
        assertThat(found.getCurrentWord()).isEqualTo(game.getCurrentWord());
        assertThat(found.getAnswers()).containsExactlyElementsOf(game.getAnswers());
    }

}
