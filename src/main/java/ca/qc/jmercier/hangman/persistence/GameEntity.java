package ca.qc.jmercier.hangman.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class GameEntity implements Serializable {

    @Id
    @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @JsonIgnore
    private String secretWord;

    private String currentWord;

    @ElementCollection(fetch = FetchType.EAGER)
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

    @JsonIgnore
    public boolean isSecretWordFound(){
        return secretWord.equalsIgnoreCase(currentWord);
    }

    @JsonIgnore
    public boolean isEnded(){
        return !Status.STARTED.equals(status);
    }
}
