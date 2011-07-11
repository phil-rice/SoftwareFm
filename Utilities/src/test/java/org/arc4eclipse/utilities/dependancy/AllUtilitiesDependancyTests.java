package org.arc4eclipse.utilities.dependancy;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllUtilitiesDependancyTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllUtilitiesDependancyTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(DependancyBuilderTest.class);
		//$JUnit-END$
		return suite;
	}

}
