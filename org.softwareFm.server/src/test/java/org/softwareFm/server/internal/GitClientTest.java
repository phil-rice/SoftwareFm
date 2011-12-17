package org.softwareFm.server.internal;

import org.softwareFm.server.ILocalGitClient;

public class GitClientTest extends AbstractLocalGitClientTest {

	@Override
	protected ILocalGitClient makeLocalGitClient() {
		return new LocalGitClient(root);
	}
}
