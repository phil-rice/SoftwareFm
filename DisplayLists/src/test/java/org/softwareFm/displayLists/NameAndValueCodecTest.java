package org.softwareFm.displayLists;

import junit.framework.TestCase;

import org.junit.Test;
import org.softwareFm.displayCore.api.ICodec;

public class NameAndValueCodecTest extends TestCase {

	@Test
	public void testEncoder() {
		checkEncoder("a$b", "a", "b");
		checkEncoder("$b", "", "b");
		checkEncoder("a$", "a", "");
		checkEncoder("$", "", "");
		checkEncoder("ab$cd$ef$g", "a$b", "cd$ef$g");
	}

	private void checkEncoder(String expected, String name, String url) {
		ICodec<NameAndValue> encoder = new NameAndValueCodec();
		String actual = encoder.toString(new NameAndValue(name, url));
		assertEquals(expected, actual);
		NameAndValue back = encoder.fromString(actual);
		String expectedName = expected.substring(0, expected.indexOf('$'));
		assertEquals(expectedName, back.name);
		assertEquals(url, back.url);
	}
}
