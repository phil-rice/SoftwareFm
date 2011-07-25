package org.arc4eclipse.utilities.aggregators;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllUtilitiesAggregatorsTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllUtilitiesAggregatorsTests.class);
		// $JUnit-BEGIN$
		suite.addTestSuite(CrossThreadsAggregatorTest.class);
		// $JUnit-END$
		return suite;
	}

}
