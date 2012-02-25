package org.softwareFm.eclipse.mysoftwareFm;

import junit.framework.TestCase;

import org.junit.Test;

public class MySoftwareFmFunctionsTest extends TestCase{

	@Test
	public void testMonthFileNameToPretty() {
		assertEquals("Jan 2012", MySoftwareFmFunctions.monthFileNameToPrettyName("january_12"));
		assertEquals("Jan 2012", MySoftwareFmFunctions.monthFileNameToPrettyName("January_12"));
		assertEquals("ab_12", MySoftwareFmFunctions.monthFileNameToPrettyName("ab_12"));
		assertEquals("abc_de", MySoftwareFmFunctions.monthFileNameToPrettyName("abc_de"));
	}

}
