package org.softwareFm.displayLists;

import org.softwareFm.utilities.strings.NameAndValue;

import junit.framework.TestCase;

public class NameAndValueTest extends TestCase{

	public void testConstructor() {
		NameAndValue nameAndValue = new NameAndValue("name", "value");
		assertEquals("name", nameAndValue.name);
		assertEquals("value", nameAndValue.value);
	}
	
	public void testFromString(){
		checkFromString("a:b", "a", "b");
		checkFromString("a:", "a", "");
		checkFromString("a", "a", null);
	}

	private void checkFromString(String raw, String name, String value) {
		NameAndValue nameAndValue =  NameAndValue.fromString(raw);
		assertEquals(name, nameAndValue.name);
		assertEquals(value, nameAndValue.value);
		
	}

}
