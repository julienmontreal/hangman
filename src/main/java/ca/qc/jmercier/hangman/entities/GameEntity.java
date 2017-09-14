package ca.qc.jmercier.hangman.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class GameEntity implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @JsonIgnore
    private String secretWord;

    private String currentWord;

    @ElementCollection
    private List<String> answers = new ArrayList<>();

    private Integer remainingAttempt;

    public GameEntity(Status status, String secretWord, String currentWord, Integer remainingAttempt) {
        this.status = status;
        this.secretWord = secretWord;
        this.currentWord = currentWord;
        this.remainingAttempt = remainingAttempt;
    }

    public void decrementRemainingAttempt(){
        remainingAttempt = remainingAttempt - 1;
        if (remainingAttempt == 0){
            status = Status.LOST;
        }
    }

}
