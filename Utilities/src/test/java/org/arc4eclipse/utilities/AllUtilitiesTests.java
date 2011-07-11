package org.arc4eclipse.utilities;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.arc4eclipse.utilities.aggregators.AllUtilitiesAggregatorsTests;
import org.arc4eclipse.utilities.collections.AllUtilitiesCollectionsTests;
import org.arc4eclipse.utilities.dependancy.AllUtilitiesDependancyTests;
import org.arc4eclipse.utilities.maps.AllUtilitiesMapsTests;
import org.arc4eclipse.utilities.monitor.AllUtilitiesMonitorTests;
import org.arc4eclipse.utilities.pooling.AllUtilitiesPoolingTests;
import org.arc4eclipse.utilities.reflection.AllUtilitiesReflectionTests;

public class AllUtilitiesTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllUtilitiesTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(AllUtilitiesAggregatorsTests.suite());
		suite.addTest(AllUtilitiesCollectionsTests.suite());
		suite.addTest(AllUtilitiesReflectionTests.suite());
		suite.addTest(AllUtilitiesPoolingTests.suite());
		suite.addTest(AllUtilitiesMapsTests.suite());
		suite.addTest(AllUtilitiesMonitorTests.suite());
		suite.addTest(AllUtilitiesDependancyTests.suite());
		// $JUnit-END$
		return suite;
	}

}
