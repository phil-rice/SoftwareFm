package org.softwareFm.server.internal;

import org.softwareFm.server.IGitServer;

public class GitServerAsLocalGitClientTest extends AbstractLocalGitClientTest {
	@Override
	protected LocalGitClient makeLocalGitClient() {
		return new GitServer(gitFacard, root);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		checkCreateRepository((IGitServer) localGitClient, "a/b");
	}
}
