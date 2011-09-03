package org.softwareFm.utilities.reflection;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllUtilitiesReflectionTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllUtilitiesReflectionTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(FieldsTest.class);
		//$JUnit-END$
		return suite;
	}

}
