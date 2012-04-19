/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.unrecognisedJar;

import java.io.File;

import junit.framework.TestCase;

public class GuessArtifactAndVersionDetailsTest extends TestCase {

	public void testGuess() {
		checkGuess("junit.jar", "junit", "");
		checkGuess("json-simple-1.1.jar", "json-simple", "1.1");
		checkGuess("log4j-1.2.16.jar", "log4j", "1.2.16");
		checkGuess("twitter4j-core-2.2.4.jar", "twitter4j-core", "2.2.4");
		checkGuess("spring-core-3.0.5.RELEASE.jar", "spring-core", "3.0.5.RELEASE");
	}

	public void _testWhenWantToGetBetter() {
		checkGuess("org.eclipse.jface_3.7.0.I20110522-1430.jar", "jface", "3.7.0");
		checkGuess("org/springframework/security/spring-security-config/3.0.7.RELEASE/spring-security-config-3.0.7.RELEASE-tests.jar", "spring-security-config", "3.0.7.RELEASE-tests");
		checkGuess("/org/hibernate/hibernate-jdbc3-testing/3.5.4-Final/hibernate-jdbc3-testing-3.5.4-Final.jar", "hibernate-jdbc3-testing", "3.5.4-Final");

	}

	public void testRt() {
		checkGuess("rt.jar", "runtime", "");
		checkGuess("C:/Program Files/Java/jdk1.6.0_24/jre/lib/rt.jar", "runtime", "1.6.0_24");
		checkGuess("C:/Program Files/Java/jdk1.6.0_24/jdk/lib/rt.jar", "runtime", "1.6.0_24");
	}

	public void testWhenDirectoryIsVersion() {
		checkGuess("C:/eclipse/WithSoftwareFm/plugins/org.junit_4.8.2.v4_8_2_v20110321-1705/junit-1.0.2.jar", "junit", "1.0.2");
		checkGuess("C:/eclipse/WithSoftwareFm/plugins/org.junit_4.8.2.v4_8_2_v20110321-1705/junit.jar", "junit", "4.8.2");
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

	private void checkGuess(String raw, String artifactId, String version) {
		GuessArtifactAndVersionDetails guesser = new GuessArtifactAndVersionDetails();
		File file = new File(raw);
		String actualArtifactId = guesser.guessArtifactName(file);
		assertEquals(artifactId, actualArtifactId);
		assertTrue(guesser.matches(artifactId, actualArtifactId));

		String actualVersion = guesser.guessVersion(file);
		assertEquals(version, actualVersion);
	}

}