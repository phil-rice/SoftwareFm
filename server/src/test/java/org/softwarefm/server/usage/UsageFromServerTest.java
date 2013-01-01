package org.softwarefm.server.usage;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Test;
import org.softwarefm.httpServer.AbstractHttpServerTest;
import org.softwarefm.server.configurator.UsageConfigurator;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.usage.IUsageFromServerCallback;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.shared.usage.UsageFromServer;
import org.softwarefm.shared.usage.UsageTestData;
import org.softwarefm.utilities.tests.Tests;

public class UsageFromServerTest extends AbstractHttpServerTest {

	private IUsageCallback callback;
	private IUsageGetter getter;
	private IUsagePersistance persistance;

	public void testGetsStatsFromUser() {
		EasyMock.expect(getter.getStats("fr1")).andReturn(UsageTestData.statsa1b3);
		EasyMock.expect(getter.getStats("fr2")).andReturn(UsageTestData.statsb1c2);
		EasyMock.replay(callback, getter);
		httpServer.start();

		UsageFromServer fromServer = new UsageFromServer("localhost", port, persistance);
		final Map<String, IUsageStats> map = Collections.synchronizedMap(new HashMap<String, IUsageStats>());
		fromServer.getStatsFor(Arrays.asList(new FriendData("fr1", null), new FriendData("fr2", "imageUrl")), new IUsageFromServerCallback() {
			@Override
			public void foundStats(String name, IUsageStats usageStats) {
				map.put(name, usageStats);
			}
		});
		Tests.assertMapEquals(map, "fr1", UsageTestData.statsa1b3, "fr2", UsageTestData.statsb1c2);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		callback = EasyMock.createMock(IUsageCallback.class);
		getter = EasyMock.createMock(IUsageGetter.class);
		persistance = IUsagePersistance.Utils.persistance();
		new UsageConfigurator(getter, callback, persistance).registerWith(httpServer);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		EasyMock.verify(callback, getter);
	}

}
