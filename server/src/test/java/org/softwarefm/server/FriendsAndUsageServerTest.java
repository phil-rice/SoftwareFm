package org.softwarefm.server;

import java.text.MessageFormat;
import java.util.Arrays;

import javax.sql.DataSource;

import org.softwarefm.httpServer.AbstractHttpServerTest;
import org.softwarefm.server.configurator.ConfiguratorConstants;
import org.softwarefm.server.configurator.FriendsConfigurator;
import org.softwarefm.server.configurator.UsageConfigurator;
import org.softwarefm.server.friend.internal.FriendManagerServer;
import org.softwarefm.server.usage.IUsageCallbackAndGetter;
import org.softwarefm.server.usage.internal.UsageMysqlCallbackAndGetter;
import org.softwarefm.shared.friend.IFriendAndFriendManager;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.shared.usage.UsageTestData;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.strings.Strings;
import org.softwarefm.utilities.tests.Tests;
import org.softwarefm.utilities.time.ITime;

public class FriendsAndUsageServerTest extends AbstractHttpServerTest {
	private final IUsagePersistance persistance = IUsagePersistance.Utils.persistance();
	private IUsageCallbackAndGetter callbackAndGetter;
	private IFriendAndFriendManager friendManager;


	public void testCanSendAndGetBackUsage() {
		checkCall(httpClient.post("usage/u1").withEntity(Strings.zip(persistance.saveUsageStats(UsageTestData.statsa1b3))));
		String actual = checkCall(httpClient.get("usage/u1"));
		IUsageStats actualstats = persistance.parse(actual);
		Tests.assertEquals(UsageTestData.statsa1b3, actualstats);
	}

	public void testAddFriends() {
		checkCall(httpClient.post(MessageFormat.format(ConfiguratorConstants.addDeleteFriendPattern, "u1", "fr1")));
		checkCall(httpClient.post(MessageFormat.format(ConfiguratorConstants.addDeleteFriendPattern, "u1", "fr2")));
		checkCall(httpClient.post(MessageFormat.format(ConfiguratorConstants.addDeleteFriendPattern, "u1", "fr2")));
		checkCall(httpClient.post(MessageFormat.format(ConfiguratorConstants.addDeleteFriendPattern, "u1", "fr3")));
		checkCall(httpClient.post(MessageFormat.format(ConfiguratorConstants.addDeleteFriendPattern, "u2", "frX")));

		Tests.assertEqualsAsSet(Arrays.asList("fr1", "fr2", "fr3"), friendManager.friendNames("u1"));
		Tests.assertEqualsAsSet(Arrays.asList("frX"), friendManager.friendNames("u2"));
	}

	public void testFriendsUsage() {
		checkCall(httpClient.post(MessageFormat.format(ConfiguratorConstants.addDeleteFriendPattern, "u1", "fr1")));
		checkCall(httpClient.post(MessageFormat.format(ConfiguratorConstants.addDeleteFriendPattern, "u1", "fr2")));
		checkCall(httpClient.post(MessageFormat.format(ConfiguratorConstants.addDeleteFriendPattern, "u2", "fr_u2")));


		checkCall(httpClient.post(MessageFormat.format(ConfiguratorConstants.userPattern, "u1")).withEntity(Strings.zip( persistance.saveUsageStats(UsageTestData.statsc1d1))));
		checkCall(httpClient.post(MessageFormat.format(ConfiguratorConstants.userPattern, "fr1")).withEntity(Strings.zip( persistance.saveUsageStats(UsageTestData.statsa1b3))));
		checkCall(httpClient.post(MessageFormat.format(ConfiguratorConstants.userPattern, "fr2")).withEntity(Strings.zip( persistance.saveUsageStats(UsageTestData.statsb1c2))));
		checkCall(httpClient.post(MessageFormat.format(ConfiguratorConstants.userPattern, "fr_u2")).withEntity(Strings.zip(persistance.saveUsageStats(UsageTestData.statsa2b1))));

		String text = checkCall(httpClient.get(MessageFormat.format(ConfiguratorConstants.friendsUsagePattern, "u1")));
		ISimpleMap<String, IUsageStats> usage = persistance.parseFriendsUsage(text);
		Tests.assertEquals(UsageTestData.fr1_a1b3_fr2_b1c2, usage);
	}


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DataSource datasource = MysqlTestData.dataSource;
		callbackAndGetter = new UsageMysqlCallbackAndGetter(datasource, ITime.Utils.system());
		friendManager = new FriendManagerServer(datasource);
		httpServer.configure(new UsageConfigurator(callbackAndGetter, callbackAndGetter, persistance));
		httpServer.configure(new FriendsConfigurator(friendManager, persistance));
		MysqlTestData.template.execute("delete from usage_table");
		MysqlTestData.template.execute("delete from friends");
		httpServer.start();
	}


}
