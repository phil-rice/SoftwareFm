package org.softwareFm.utilities.pooling;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllUtilitiesPoolingTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllUtilitiesPoolingTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(ArraySimpleMapPoolTest.class);
		suite.addTestSuite(ObjectPoolTest.class);
		suite.addTestSuite(ThreadSafePoolTest.class);
		suite.addTestSuite(ByteArraySimpleStringPoolTest.class);
		suite.addTestSuite(ByteBufferSimpleStringPoolTest.class);
		suite.addTestSuite(UnsafePoolTest.class);
		// $JUnit-END$
		return suite;
	}

}
