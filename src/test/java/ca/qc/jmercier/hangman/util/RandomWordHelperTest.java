package ca.qc.jmercier.hangman.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RandomWordHelperTest {

	@Autowired
	private RandomWordHelper helper;

	@Value("#{'${hangman.wordList}'.split(',')}")
	private List<String> wordList;

	@Test
	public void getRandomWord(){
		String randomWord = helper.getRandomWord();
		assertNotNull(randomWord);
		assertNotEquals("",randomWord);
		assertTrue(wordList.stream().anyMatch(s -> s.equals(randomWord)));
	}
}
