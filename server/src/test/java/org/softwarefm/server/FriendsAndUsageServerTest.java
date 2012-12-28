package org.softwarefm.server;

import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.http.HttpStatus;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.shared.usage.UsageTestData;
import org.softwarefm.utilities.callbacks.MemoryCallback;
import org.softwarefm.utilities.http.IHttpClient;
import org.softwarefm.utilities.http.IResponse;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.strings.Strings;
import org.softwarefm.utilities.tests.Tests;

public class FriendsAndUsageServerTest extends TestCase {
	private final int port = 8190;
	protected MemoryCallback<Throwable> memoryThrowable;
	private FriendsAndUsageServer server;
	private IHttpClient client;
	private final IUsagePersistance persistance = IUsagePersistance.Utils.persistance();

	public void testCanShutdown() {
	}

	public void testCanSendAndGetBackUsage() {
		checkCall(client.post("usage/u1").withEntity(Strings.zip(persistance.save(UsageTestData.statsa1b3))));
		String actual = checkCall(client.get("usage/u1"));
		IUsageStats actualstats = persistance.parse(actual);
		Tests.assertEquals(UsageTestData.statsa1b3, actualstats);
	}

	public void testAddFriends() {
		checkCall(client.post("user/u1/fr1"));
		checkCall(client.post("user/u1/fr2"));
		checkCall(client.post("user/u1/fr2"));
		checkCall(client.post("user/u1/fr3"));
		checkCall(client.post("user/u2/frX"));

		Tests.assertEqualsAsSet(Arrays.asList("fr1", "fr2", "fr3"), server.friendManager.friendNames("u1"));
		Tests.assertEqualsAsSet(Arrays.asList("frX"), server.friendManager.friendNames("u2"));
	}

	public void testFriendsUsage() {
		checkCall(client.post("user/u1/fr1"));
		checkCall(client.post("user/u1/fr2"));
		checkCall(client.post("user/u2/fr_u2"));

		checkCall(client.post("usage/u1").withEntity(Strings.zip( persistance.save(UsageTestData.statsc1d1))));
		checkCall(client.post("usage/fr1").withEntity(Strings.zip( persistance.save(UsageTestData.statsa1b3))));
		checkCall(client.post("usage/fr2").withEntity(Strings.zip( persistance.save(UsageTestData.statsb1c2))));
		checkCall(client.post("usage/fr_u2").withEntity(Strings.zip(persistance.save(UsageTestData.statsa2b1))));

		String text = checkCall(client.get("user/friendsUsage/u1"));
		ISimpleMap<String, IUsageStats> usage = persistance.parseFriendsUsage(text);
		Tests.assertEquals(UsageTestData.fr1_a1b3_fr2_b1c2, usage);
	}

	private String checkCall(IHttpClient client) {
		IResponse response = client.execute();
		assertEquals(response.toString(), HttpStatus.SC_OK, response.statusCode());
		return response.asString();

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		MysqlTestData.template.execute("delete from usage_table");
		MysqlTestData.template.execute("delete from friends");
		memoryThrowable = new MemoryCallback<Throwable>();
		server = new FriendsAndUsageServer(port, MysqlTestData.dataSource, memoryThrowable);
		client = IHttpClient.Utils.builder().host("localhost", port);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (server != null)
			server.shutdown();
		memoryThrowable.assertNotCalled();
	}

}
