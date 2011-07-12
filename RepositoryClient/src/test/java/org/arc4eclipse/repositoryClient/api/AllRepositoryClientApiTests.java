package org.arc4eclipse.repositoryClient.api;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllRepositoryClientApiTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllRepositoryClientApiTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(IJarDetailsTest.class);
		// $JUnit-END$
		return suite;
	}
}
