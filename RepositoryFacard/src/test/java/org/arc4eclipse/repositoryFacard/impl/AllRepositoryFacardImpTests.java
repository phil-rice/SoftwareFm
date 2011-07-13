package org.arc4eclipse.repositoryFacard.impl;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllRepositoryFacardImpTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllRepositoryFacardImpTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(AspectToParametersTest.class);
		suite.addTestSuite(RepositoryFacardTest.class);
		// $JUnit-END$
		return suite;
	}
}
