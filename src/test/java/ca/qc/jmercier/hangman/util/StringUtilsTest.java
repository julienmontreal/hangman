package ca.qc.jmercier.hangman.util;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void getUnderscoreString(){
		String word =  "framework";
		String underscoreString = StringUtils.getUnderscoreString(word);
		Assert.assertEquals(underscoreString, "_________");
	}

	@Test
	public void replaceLetter(){
		String secret =  "framework";
		String current = "f________";
		String newCurrent = StringUtils.replaceLetter(secret, current, 'w');
		Assert.assertEquals(newCurrent, "f____w___");
	}

	@Test
	public void replaceLetter_DoubleOccurence(){
		String secret =  "framework";
		String current = "f________";
		String newCurrent = StringUtils.replaceLetter(secret, current, 'r');
		Assert.assertEquals(newCurrent, "fr_____r_");
	}

	@Test
	public void replaceLetter_NotExisting(){
		String secret =  "framework";
		String current = "f________";
		String newCurrent = StringUtils.replaceLetter(secret, current, 'x');
		Assert.assertEquals(newCurrent, "f________");
	}
}
