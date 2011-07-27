package org.arc4eclipse.displayCore.api.impl;

import java.util.Map.Entry;

import junit.framework.TestCase;

import org.arc4eclipse.displayCore.api.NameSpaceNameAndValue;
import org.arc4eclipse.displayCore.api.impl.Entries;
import org.arc4eclipse.utilities.maps.Maps;

public class EntriesTest extends TestCase {

	public void testRipper() {
		checkRipper("", "", "");
		checkRipper("a", "a", "a");
		checkRipper("a:b", "a", "b");
		checkRipper(":b", "", "b");
	}

	private void checkRipper(String nameAndNameSpace, String nameSpace, String name) {
		Entry<String, Object> entry = Maps.<String, Object> makeMap(nameAndNameSpace, "value").entrySet().iterator().next();
		NameSpaceNameAndValue result = Entries.rip(entry);
		assertEquals(nameSpace, result.nameSpace);
		assertEquals(name, result.name);
		assertEquals("value", result.value);
	}

}
