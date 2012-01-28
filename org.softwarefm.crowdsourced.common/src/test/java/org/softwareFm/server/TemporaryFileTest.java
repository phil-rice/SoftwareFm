package org.softwareFm.server;

import java.io.File;

import junit.framework.TestCase;

import org.softwareFm.utilities.collections.Files;
import org.softwareFm.utilities.tests.Tests;

abstract public class TemporaryFileTest extends TestCase {
	protected File root;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		root = Tests.makeTempDirectory(getClass().getSimpleName());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (root.exists())
			assertTrue(Files.deleteDirectory(root));
	}
}
