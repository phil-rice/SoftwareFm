package org.softwareFm.common.server;

import java.io.File;

import junit.framework.TestCase;

import org.softwareFm.common.collections.Files;
import org.softwareFm.common.tests.Tests;

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
