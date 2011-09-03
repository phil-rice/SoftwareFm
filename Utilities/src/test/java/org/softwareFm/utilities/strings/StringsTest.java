package org.softwareFm.utilities.strings;

import junit.framework.TestCase;

public class StringsTest extends TestCase {

	public void testKeepOnly() {
		assertEquals("1234", Strings.onlyKeep("raw12and3,4", "4231"));
	}

}
