package ca.qc.jmercier.hangman.util;

import java.util.stream.IntStream;

public class StringUtils {

	private static final String UNDERSCORE = "_";

	private StringUtils() {
	}

	public static String getUnderscoreString(String word) {
		StringBuilder stringBuilder = new StringBuilder(word.length());
		IntStream.range(0, word.length())
				.forEach(i -> stringBuilder.append(UNDERSCORE));
		return stringBuilder.toString();
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
