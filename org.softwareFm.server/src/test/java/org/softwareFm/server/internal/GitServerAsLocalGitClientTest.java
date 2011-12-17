package org.softwareFm.server.internal;

import org.softwareFm.server.ILocalGitClient;

public class GitServerAsLocalGitClientTest extends AbstractLocalGitClientTest {
	@Override
	protected ILocalGitClient makeLocalGitClient() {
		return new GitServer(gitFacard, root, "not used");
	}

}
