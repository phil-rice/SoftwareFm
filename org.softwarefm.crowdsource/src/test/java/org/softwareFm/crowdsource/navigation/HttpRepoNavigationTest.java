package org.softwareFm.crowdsource.navigation;

import org.softwareFm.crowdsource.api.server.AbstractProcessorDatabaseIntegrationTests;

public class HttpRepoNavigationTest extends AbstractProcessorDatabaseIntegrationTests {

	public void testHttpRepo() {
		AbstractRepoNavigationTest.setUpFixture(remoteOperations);
		HttpRepoNavigation navigation = new HttpRepoNavigation(getLocalUserAndGroupsContainer());
		AbstractRepoNavigationTest.checkValues(navigation);
		AbstractRepoNavigationTest.checkValues(navigation);
	}
	
}
