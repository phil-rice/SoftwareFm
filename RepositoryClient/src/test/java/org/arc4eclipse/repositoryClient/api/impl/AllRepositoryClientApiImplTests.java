package org.arc4eclipse.repositoryClient.api.impl;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllRepositoryClientApiImplTests {
	public static Test suite() {
		TestSuite suite = new TestSuite(AllRepositoryClientApiImplTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(RepositoryClientTest.class);
		suite.addTestSuite(JarDetailsTest.class);
		// $JUnit-END$
		return suite;
	}

}
