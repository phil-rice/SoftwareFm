package org.softwarefm.server.friend.internal;

import org.easymock.EasyMock;
import org.softwarefm.eclipse.usage.UsageStatData;
import org.softwarefm.server.AbstractServerHandlerTest;
import org.softwarefm.shared.friend.IFriendAndFriendManager;
import org.softwarefm.shared.usage.UsageTestData;
import org.softwarefm.utilities.http.IResponse;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.maps.SimpleMaps;

public class FriendServerHandlerTest extends AbstractServerHandlerTest<FriendServerHandler> {

	private IFriendAndFriendManager manager;

	@Override
	protected FriendServerHandler createHandler() {
		return new FriendServerHandler(manager = EasyMock.createMock(IFriendAndFriendManager.class));
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
		EasyMock.expect(manager.friendsUsage("u1")).andReturn(makeMap("fr1", UsageTestData.statsa1b3));
		replay();
		
		httpServer.start();
		IResponse response = httpClient.get("friendsUsage/u1").execute();
		assertEquals(200, response.statusCode());
	}


	private ISimpleMap<String, ISimpleMap<String, UsageStatData>> makeMap(Object...nameAndStats) {
		return SimpleMaps.makeMap(nameAndStats);
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
