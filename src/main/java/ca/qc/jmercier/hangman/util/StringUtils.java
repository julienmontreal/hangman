package ca.qc.jmercier.hangman.util;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StringUtils {

	private static final String UNDERSCORE = "_";

		public static String getUnderscoreString(String word) {
		StringBuffer stringBuffer = new StringBuffer(word.length());
		IntStream.range(0, word.length())
				.forEach(i -> stringBuffer.append(UNDERSCORE));
		return stringBuffer.toString();
	}

	public static String replaceLetter(String secret, String currentWord, char letter){
		char[] secretCharArray = secret.toCharArray();
		char[] currentWordCharArray = currentWord.toCharArray();
		IntStream.range(0, secretCharArray.length)
				.filter(i -> secretCharArray[i] == letter)
				.forEach(i -> currentWordCharArray[i] = letter);
		return new String(currentWordCharArray);
	}
}
