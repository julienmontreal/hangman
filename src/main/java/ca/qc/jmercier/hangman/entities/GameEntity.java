package ca.qc.jmercier.hangman.entities;

import ca.qc.jmercier.hangman.dto.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@Entity
@Getter
@Setter
public class GameEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @JsonIgnore
    private String secretWord;

    private String currentWord;

    @JsonIgnore
    @ElementCollection
    private List<String> answers;

    private Integer remainingAttempt;

    public void decrementRemainingAttempt(){
        remainingAttempt = remainingAttempt - 1;
        if (remainingAttempt == 0 && !Status.WON.equals(status)){
            status = Status.LOST;
        }
    }

}
