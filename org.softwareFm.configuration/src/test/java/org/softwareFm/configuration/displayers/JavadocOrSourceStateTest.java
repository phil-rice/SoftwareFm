package org.softwareFm.configuration.displayers;

import junit.framework.TestCase;

public class JavadocOrSourceStateTest extends TestCase {

	public void testHasEclipse() {
		checkHasXX("eclipse", true, null, false);
		checkHasXX("eclipse", true, "", false);
		checkHasXX("eclipse", true, "softwareFm", true);
	}

	public void testHasSoftwareFm() {
		checkHasXX("eclipse", true, "softwareFm", true);
		checkHasXX("eclipse", true, "", false);
		checkHasXX("eclipse", true, null, false);
	}

	public void testMatches() {
		checkMatches("value", "value", true);

		checkMatches("eclipse", "softwareFm", false);
		checkMatches("eclipse", "", false);
		checkMatches("eclipse", null, false);
		checkMatches("", "softwareFm", false);
		checkMatches(null, "softwareFm", false);

	}

	public void testMatchesOnlyWhenHasBoth() {
		checkMatches("", "", false);
		checkMatches(null, null, false);
	}

	public void testEclipseValueIsJar() {
		checkEclipseValueIsJar("http://eclipse.jar", null, true);
		checkEclipseValueIsJar("eclipse.jar", null, true);

		checkEclipseValueIsJar("eclipse", null, false);
		checkEclipseValueIsJar("", null, false);
		checkEclipseValueIsJar(null, null, false);
	}

	public void testEclipseValueIsHttp() {
		checkEclipseValueIsHttp("http://eclipse.jar", null, true);
		checkEclipseValueIsHttp("file://eclipse.jar", null, false);
		checkEclipseValueIsHttp("eclipse.jar", null, false);
		checkEclipseValueIsHttp("", null, false);
		checkEclipseValueIsHttp(null, null, false);
	}

	private void checkEclipseValueIsJar(String eclipse, String softwareFm, boolean expected) {
		JavadocOrSourceState state = new JavadocOrSourceState(eclipse, softwareFm);
		assertEquals(expected, state.eclipseValueIsJar);

	}

	private void checkEclipseValueIsHttp(String eclipse, String softwareFm, boolean expected) {
		JavadocOrSourceState state = new JavadocOrSourceState(eclipse, softwareFm);
		assertEquals(expected, state.eclipseValueIsHttp);

	}

	private void checkMatches(String eclipse, String softwareFm, boolean expected) {
		JavadocOrSourceState state = new JavadocOrSourceState(eclipse, softwareFm);
		assertEquals(expected, state.matches);

	}

	private void checkHasXX(String eclipse, boolean hasEclipse, String softwareFm, boolean hasSoftwareFm) {
		JavadocOrSourceState state = new JavadocOrSourceState(eclipse, softwareFm);
		assertEquals(hasEclipse, state.hasEclipse);
		assertEquals(hasSoftwareFm, state.hasSoftwareFm);
	}

}
