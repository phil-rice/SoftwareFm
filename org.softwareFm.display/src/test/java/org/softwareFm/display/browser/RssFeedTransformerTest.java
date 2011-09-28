package org.softwareFm.display.browser;

import junit.framework.TestCase;

import org.softwareFm.utilities.collections.Files;

public class RssFeedTransformerTest extends TestCase{

	public void testTransfroms() throws Exception {
		checkTransforms("sampleThatDoesntFormat");
	}

	private void checkTransforms(String string) throws Exception {
		String input = Files.getTextFromClassPath(getClass(), string + ".xml");
		String expected = Files.getTextFromClassPath(getClass(), string + ".html").replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", "");
		String actual = new RssFeedTransformer().apply(input).replaceAll(" ","").replaceAll("\n", "").replaceAll("\r", "");
		assertEquals(expected, actual);
	}

}
