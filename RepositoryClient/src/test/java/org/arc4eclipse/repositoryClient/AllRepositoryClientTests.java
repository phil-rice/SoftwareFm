package org.arc4eclipse.repositoryClient;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.arc4eclipse.repositoryClient.api.AllRepositoryClientApiTests;

public class AllRepositoryClientTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllRepositoryClientTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(AllRepositoryClientApiTests.suite());
		// $JUnit-END$
		return suite;
	}
}
