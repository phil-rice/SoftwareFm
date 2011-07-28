package org.arc4eclipse.displayCore.api.impl;

import junit.framework.TestCase;

import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.displayCore.api.NameSpaceNameAndValue;
import org.arc4eclipse.displayCore.api.NameSpaceNameValueAndDisplayer;

public class NameSpaceAndNameEtcTests extends TestCase {

	public void testRipper() {
		checkRipper("", "", "");
		checkRipper("a", "a", "a");
		checkRipper("a:b", "a", "b");
		checkRipper(":b", "", "b");
	}

	public void testNameSpaceAndName() {
		NameSpaceAndName nameSpaceAndName = new NameSpaceAndName("key", "ns1", "n1");
		assertEquals("key", nameSpaceAndName.key);
		assertEquals("ns1", nameSpaceAndName.nameSpace);
		assertEquals("n1", nameSpaceAndName.name);
	}

	public void testNameSpaceNameAndValue() {
		NameSpaceNameAndValue nameSpaceAndName = new NameSpaceNameAndValue("key", "ns1", "n1", "val1");
		assertEquals("key", nameSpaceAndName.key);
		assertEquals("ns1", nameSpaceAndName.nameSpace);
		assertEquals("n1", nameSpaceAndName.name);
		assertEquals("val1", nameSpaceAndName.value);
	}

	public void testNameSpaceNameValueAndDispluer() {
		DisplayUnknown displayUnknown = new DisplayUnknown();
		NameSpaceNameValueAndDisplayer nameSpaceAndName = new NameSpaceNameValueAndDisplayer(new NameSpaceAndName("key", "ns1", "n1"), "val1", displayUnknown);
		assertEquals("key", nameSpaceAndName.key);
		assertEquals("ns1", nameSpaceAndName.nameSpace);
		assertEquals("n1", nameSpaceAndName.name);
		assertEquals("val1", nameSpaceAndName.value);
		assertSame(displayUnknown, nameSpaceAndName.displayer);
	}

	private void checkRipper(String nameAndNameSpace, String nameSpace, String name) {
		NameSpaceAndName result = NameSpaceAndName.Utils.rip(nameAndNameSpace);
		assertEquals(nameSpace, result.nameSpace);
		assertEquals(name, result.name);
	}

}
