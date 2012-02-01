package org.softwareFm.swt.unrecognisedJar;

import java.io.File;

import org.softwareFm.swt.unrecognisedJar.GuessArtifactAndVersionDetails;

import junit.framework.TestCase;

public class GuessArtifactNameTest extends TestCase {

	public void testGuess() {
		checkGuess("rt", "rt");
		checkGuess("junit", "junit");
		checkGuess("spring-core", "spring-core-3.0.5.RELEASE");
		checkGuess("json-simple", "json-simple-1.1");
		checkGuess("log4j", "log4j-1.2.16");
		checkGuess("log4j", "log4j_1.2.16");
		checkGuess("twitter4j-core", "twitter4j-core-2.2.4");
	}

	public void testMatcher() {
		GuessArtifactAndVersionDetails guesser = new GuessArtifactAndVersionDetails();
		assertEquals(true, guesser.matches("abc", "abc"));
		assertEquals(false, guesser.matches("abc", "abcd"));
		assertEquals(true, guesser.matches("rt", "rt"));
		assertEquals(true, guesser.matches("rt", "runtime"));
		assertEquals(false, guesser.matches("rt", "runtime1"));
		assertEquals(false, guesser.matches("rt1", "runtime"));
	}


	private void checkGuess(String expected, String raw) {
		GuessArtifactAndVersionDetails guesser = new GuessArtifactAndVersionDetails();
		String actual = guesser.guessArtifactName(new File(raw + ".something"));
		assertEquals(expected, actual);
		assertTrue(guesser.matches(expected, actual));
	}

}
