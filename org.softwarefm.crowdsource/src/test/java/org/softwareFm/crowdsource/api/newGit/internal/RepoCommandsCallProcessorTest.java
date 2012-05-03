package org.softwareFm.crowdsource.api.newGit.internal;

import org.junit.Test;
import org.softwareFm.crowdsource.api.internal.Container;
import org.softwareFm.crowdsource.api.newGit.IAccessControlList;
import org.softwareFm.crowdsource.api.newGit.IRepoData;

public class RepoCommandsCallProcessorTest extends RepoTest {

	private Container container;

	@Test
	public void test() {
		fail("Not yet implemented");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		container = new Container(transactionManager, null);
		IRepoData.Utils.configureLocalContainer(container, localRoot, remoteAsUri, repoLocator, IAccessControlList.Utils.noAccessControl());
	}

}
