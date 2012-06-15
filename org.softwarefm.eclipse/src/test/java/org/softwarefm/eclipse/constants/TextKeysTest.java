package org.softwarefm.eclipse.constants;

import junit.framework.TestCase;

import org.softwarefm.eclipse.Marker;
import org.softwarefm.utilities.tests.Tests;

public class TextKeysTest extends TestCase {

	public void test() {
		Tests.checkResourceBundle(Marker.class, "text", TextKeys.class);
	}

}
