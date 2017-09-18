package ca.qc.jmercier.hangman.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AlreadyAnsweredTwiceException extends RuntimeException {

	public AlreadyAnsweredTwiceException(String message) {
		super(message);
	}
}
