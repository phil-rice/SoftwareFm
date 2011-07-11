package org.arc4eclipse.utilities.collections;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllUtilitiesCollectionsTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllUtilitiesCollectionsTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(IterablesTest.class);
		suite.addTestSuite(AbstractFindNextIterableTest.class);
		suite.addTestSuite(SimpleListsTest.class);
		suite.addTestSuite(ListsTest.class);
		suite.addTestSuite(ReusableSimpleListTest.class);
		//$JUnit-END$
		return suite;
	}

}
