package org.softwareFm.crowdsource.navigation;

public class ServerRepoNavigationTest extends AbstractRepoNavigationTest {

	public void test() {
		setUpFixture(remoteOperations);
		checkValues(new ServerRepoNavigation(remoteRoot));
	}

}
