package org.softwarefm.display;

import java.io.File;

import junit.framework.Test;

import org.softwareFm.utilities.reflection.IClassAcceptor;
import org.softwareFm.utilities.tests.Tests;

public class AllSoftwareFmDisplayTests {

	public static Test suite() {
		return Tests.makeSuiteUnder(AllSoftwareFmDisplayTests.class, new File("."), IClassAcceptor.Utils.isTest());
	}
}
