package org.softwareFm.utilities.maps;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllUtilitiesMapsTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllUtilitiesMapsTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(MapsTest.class);
		suite.addTestSuite(ArraySimpleMapTest.class);
		// $JUnit-END$
		return suite;
	}

}
