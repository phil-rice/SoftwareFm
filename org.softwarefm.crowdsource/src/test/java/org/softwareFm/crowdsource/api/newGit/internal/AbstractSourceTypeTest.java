package org.softwareFm.crowdsource.api.newGit.internal;



abstract public class AbstractSourceTypeTest extends RepoTest{

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	
	}

	@Override
	protected void tearDown() throws Exception {
		linkedRepoData.rollback();
		super.tearDown();
	}

}
