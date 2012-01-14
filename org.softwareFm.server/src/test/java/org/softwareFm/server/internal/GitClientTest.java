package org.softwareFm.server.internal;

import org.softwareFm.server.ILocalGitClient;
import org.softwareFm.utilities.tests.Tests;

public class GitClientTest extends AbstractLocalGitClientTest {

	@Override
	protected ILocalGitClient makeLocalGitClient() {
		return new LocalGitClient(root);
	}

	public static void main(String[] args) {
		while (true)
			Tests.executeTest(GitClientTest.class);
	}
}
