package org.softwareFm.utilities.strings;

import junit.framework.TestCase;

public class StringsTest extends TestCase {

	public void testSplit(){
		assertEquals(new PreAndPost("a", "b"), Strings.split("a.b", '.'));
		assertEquals(new PreAndPost("a", "b"), Strings.split("a$b", '$'));
		assertEquals(new PreAndPost("ab", null), Strings.split("ab", '$'));
		assertEquals(new PreAndPost("ab", ""), Strings.split("ab$", '$'));
		assertEquals(new PreAndPost("", null), Strings.split("", '$'));
		assertEquals(new PreAndPost("", ""), Strings.split(".", '.'));
	}
	
	public void testKeepOnly() {
		assertEquals("1234", Strings.onlyKeep("raw12and3,4", "4231"));
	}

}
