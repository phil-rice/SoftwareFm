/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.unrecognisedJar;

import java.io.File;

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