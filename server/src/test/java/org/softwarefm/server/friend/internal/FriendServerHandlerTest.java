package org.softwarefm.server.friend.internal;

import org.easymock.EasyMock;
import org.softwarefm.server.AbstractServerHandlerTest;
import org.softwarefm.shared.friend.IFriendAndFriendManager;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.shared.usage.UsageTestData;
import org.softwarefm.utilities.http.IResponse;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.tests.Tests;

public class FriendServerHandlerTest extends AbstractServerHandlerTest<FriendServerHandler> {

	private IFriendAndFriendManager manager;
	private final IUsagePersistance persistance = IUsagePersistance.Utils.persistance();

	@Override
	protected FriendServerHandler createHandler() {
		return new FriendServerHandler(persistance, manager = EasyMock.createMock(IFriendAndFriendManager.class));
	}

	public void testAdd() {
		manager.add("u1", "fr1_1");
		replay();

		httpServer.start();
		assertEquals(200, httpClient.post("somePrefix/user/u1/fr1_1").execute().statusCode());
	}

	public void testDelete() {
		manager.delete("u1", "fr1_1");
		replay();

		httpServer.start();
		assertEquals(200, httpClient.delete("somePrefix/user/u1/fr1_1").execute().statusCode());
	}

	public void testGetReturnsAllUsageAboutAllFriends() {
		EasyMock.expect(manager.friendsUsage("u1")).andReturn(UsageTestData.fr1_a1b3_fr2_b1c2);
		replay();
		
		httpServer.start();
		IResponse response = httpClient.get("friendsUsage/u1").execute();
		assertEquals(200, response.statusCode());
		ISimpleMap<String, IUsageStats> actual = persistance.parseFriendsUsage(response.asString());
		Tests.assertEquals(UsageTestData.fr1_a1b3_fr2_b1c2, actual);
	}

	private void replay() {
		EasyMock.replay(manager);
	}

	@Override
	protected void tearDown() throws Exception {
		EasyMock.verify(manager);
		super.tearDown();
	}

}
