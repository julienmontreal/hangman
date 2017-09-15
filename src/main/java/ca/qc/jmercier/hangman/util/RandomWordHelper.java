package ca.qc.jmercier.hangman.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class RandomWordHelper {

	@Value("#{'${hangman.wordList}'.split(',')}")
	private List<String> wordList;

	public String getRandomWord(){
		Random random = new Random();
		return wordList.get(random.nextInt(wordList.size()));
	}
}
