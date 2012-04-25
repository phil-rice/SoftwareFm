package org.softwareFm.crowdsource.api.newGit.internal;



abstract public class AbstractSourceTypeTest extends RepoTest{

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	
	}

	@Override
	protected void tearDown() throws Exception {
		repoData.rollback();
		super.tearDown();
	}

}
