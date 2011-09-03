package org.softwareFm.allTests;

import java.io.File;

import junit.framework.Test;

import org.softwareFm.utilities.reflection.IClassAcceptor;
import org.softwareFm.utilities.tests.Tests;

public class AllTests {

	public static Test suite() {
		return Tests.makeSuiteUnder(AllTests.class, new File(".."), IClassAcceptor.Utils.isTest());
	}
}
