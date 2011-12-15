package org.softwareFm.server.internal;

public class GitServerAsLocalGitClientTest extends AbstractLocalGitClientTest {
	@Override
	protected LocalGitClient makeLocalGitClient() {
		return new GitServer(root);
	}
}
