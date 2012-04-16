package org.softwareFm.crowdsource.navigation;

public class CachedServerRepoNavigationTest extends AbstractRepoNavigationTest {

	private ServerRepoNavigation repoNavigation;
	private MeteredRepoNavigation meteredNavigation;
	private CachedServerRepoNavigation cachedRepoNavigation;

	public void testCacheReturnsSameResultsButOnlyGetsResultsFromDelegateOnce() {
		setUpFixture(remoteOperations);
		checkValues(cachedRepoNavigation);
		checkValues(cachedRepoNavigation);
		checkValues(cachedRepoNavigation);
		for (String url : urls)
			assertEquals(1, meteredNavigation.countFor(url));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		repoNavigation = new ServerRepoNavigation(remoteRoot);
		meteredNavigation = new MeteredRepoNavigation(repoNavigation);
		cachedRepoNavigation = new CachedServerRepoNavigation(meteredNavigation);
	}

}
