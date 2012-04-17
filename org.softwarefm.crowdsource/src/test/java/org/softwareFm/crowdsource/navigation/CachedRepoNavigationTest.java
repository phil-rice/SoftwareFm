package org.softwareFm.crowdsource.navigation;

import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.utilities.runnable.Callables;

public class CachedRepoNavigationTest extends AbstractRepoNavigationTest {

	private ServerRepoNavigation repoNavigation;
	private MeteredRepoNavigation meteredNavigation;

	public void testCacheReturnsSameResultsButOnlyGetsResultsFromDelegateOnce() {
		setUpFixture(remoteOperations);
		CachedRepoNavigation cachedRepoNavigation = new CachedRepoNavigation(meteredNavigation, 1000, Callables.value(10L));
		checkValues(cachedRepoNavigation);
		checkValues(cachedRepoNavigation);
		checkValues(cachedRepoNavigation);
		for (String url : urls)
			assertEquals(1, meteredNavigation.countFor(url));
	}

	public void testStaleCache() {
		setUpFixture(remoteOperations);

		Callable<Long> timeGetter = Callables.valueFromList(0L, 0L, 11L, 11L, 22L, 22L);
		CachedRepoNavigation navigation = new CachedRepoNavigation(meteredNavigation, 10, timeGetter);

		getUrlAndCheckCount(navigation, 1);// second 0L
		getUrlAndCheckCount(navigation, 2);// first 11lL
		getUrlAndCheckCount(navigation, 2);// second 11lL
		getUrlAndCheckCount(navigation, 3);// first 21L
		getUrlAndCheckCount(navigation, 3);// second 21lL
	}

	public void testClear() {
		setUpFixture(remoteOperations);

		CachedRepoNavigation cachedRepoNavigation = new CachedRepoNavigation(meteredNavigation, 1000, Callables.value(10L));
		checkValues(cachedRepoNavigation);
		checkValues(cachedRepoNavigation);
		for (String url : urls)
			assertEquals(1, meteredNavigation.countFor(url));
		cachedRepoNavigation.clearCaches();
		checkValues(cachedRepoNavigation);
		checkValues(cachedRepoNavigation);
		for (String url : urls)
			assertEquals(2, meteredNavigation.countFor(url));
	}

	public void testClearUrl() {
		setUpFixture(remoteOperations);

		CachedRepoNavigation cachedRepoNavigation = new CachedRepoNavigation(meteredNavigation, 1000, Callables.value(10L));
		checkValues(cachedRepoNavigation);
		checkValues(cachedRepoNavigation);
		cachedRepoNavigation.clearCache("a/b/c");
		checkValues(cachedRepoNavigation);
		checkValues(cachedRepoNavigation);
		for (String url : new String[] { "a/b/d", "a/b/e/f", "a/b/e/g", "a/h", "a/h/i", "a/h/j", "k" })
			assertEquals(1, meteredNavigation.countFor(url));
		for (String url : new String[] { "a", "a/b", "a/b/c", })
			assertEquals(2, meteredNavigation.countFor(url));
	}

	@SuppressWarnings("unchecked")
	private void getUrlAndCheckCount(CachedRepoNavigation navigation, int count) {
		String url = "a/b";
		checkGetUrl(navigation, url, a, b);// uses second 0L
		assertEquals(count, meteredNavigation.countFor(url));
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		repoNavigation = new ServerRepoNavigation(remoteRoot);
		meteredNavigation = new MeteredRepoNavigation(repoNavigation);
	}

}
