package org.arc4eclipse.allTests;
import java.io.File;

import junit.framework.Test;

import org.apache.log4j.Logger;
import org.arc4eclipse.utilities.reflection.IClassAcceptor;
import org.arc4eclipse.utilities.tests.Tests;

public class AllTests {

	public static Test suite() {
		return Tests.makeSuiteUnder(AllTests.class, new File(".."), IClassAcceptor.Utils.isTest());
	}
}
