package org.softwareFm.configuration.displayers;

import junit.framework.TestCase;

import org.junit.Test;

public class JavadocOrSourceButtonTitleCalculatorTest extends TestCase {

	private final Runnable copyToEclipse = new Runnable() {
		@Override
		public void run() {
		}
	};
	private final Runnable copyToSoftwareFm = new Runnable() {
		@Override
		public void run() {
		}
	};

	@Test
	public void testTitle() throws Exception {
		checkTitle("eclipse", "softwareFm", "button.doesntMatches.title", null);
		checkTitle("value", "value", "button.matches.title", null);

		checkTitle("eclipse", null, "button.eclipseNotUrl.title", null);
		checkTitle("http://eclipse", null, "button.copyToSoftwareFm.title", copyToSoftwareFm);
		checkTitle("http://eclipse.jar", null, "button.copyToSoftwareFm.title", copyToSoftwareFm);
		checkTitle("http://eclipse.html", null, "button.copyToSoftwareFm.title", copyToSoftwareFm);

		checkTitle(null, null, "button.noData.title", null);

		checkTitle(null, "softwarefm", "button.copyToEclipse.title", copyToEclipse);

	}

	private void checkTitle(String eclipseValue, String softwareFmValue, String title, Runnable runnable) throws Exception {
		TitleAndRunnable actual = new JavadocOrSourceButtonTitleCalculator(copyToEclipse, copyToSoftwareFm).apply(new JavadocOrSourceState(eclipseValue, softwareFmValue));
		assertEquals(title, actual.title);
		assertSame(runnable, actual.runnable);

	}

}
