package org.arc4eclipse.utilities.monitor;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllUtilitiesMonitorTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllUtilitiesMonitorTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(SysoutMonitorTests.class);
		//$JUnit-END$
		return suite;
	}

}
