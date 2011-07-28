package org.arc4eclipse.displayCore.api.impl;

import junit.framework.TestCase;

import org.arc4eclipse.displayCore.api.ITitleLookup;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.displayCore.api.NameSpaceNameValueAndDisplayer;

public class TitleLookupTest extends TestCase {

	public void testComparatorCurrentlyJustDoesAlphabeticNameSpaceWhenName() {
		checkComparator("ns1", "n1", "ns2", "n2", -1);
		checkComparator("ns2", "n2", "ns1", "n1", 1);
		checkComparator("ns1", "n1", "ns1", "n2", -1);
		checkComparator("ns1", "n2", "ns1", "n1", 1);
		checkComparator("ns1", "n1", "ns1", "n1", 0);
	}

	private void checkComparator(String ns1, String n1, String ns2, String n2, int expected) {
		NameSpaceNameValueAndDisplayer val1 = new NameSpaceNameValueAndDisplayer(new NameSpaceAndName("", ns1, n1), null, null);
		NameSpaceNameValueAndDisplayer val2 = new NameSpaceNameValueAndDisplayer(new NameSpaceAndName("", ns2, n2), null, null);
		ITitleLookup titleLookup = ITitleLookup.Utils.titleLookup();
		assertEquals(expected, titleLookup.compare(val1, val2));

	}

}
