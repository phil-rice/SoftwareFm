package org.softwarefm.server.configurator;

import java.text.MessageFormat;
import java.util.Arrays;

import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.easymock.EasyMock;
import org.softwarefm.httpServer.StatusAndEntity;
import org.softwarefm.shared.friend.IFriendAndFriendManager;
import org.softwarefm.shared.usage.UsageTestData;
import org.softwarefm.utilities.http.HttpMethod;
import org.softwarefm.utilities.strings.Strings;
import org.softwarefm.utilities.tests.Tests;

public class FriendsConfiguratorWithMocksTest extends AbstractFriendsConfiguratorTest {

	private IFriendAndFriendManager manager;

	public void testListFriends() throws Exception {
		EasyMock.expect(manager.friendNames("u1")).andReturn(Arrays.asList("fr1_1", "fr1_2"));
		EasyMock.replay(manager);

		StatusAndEntity statusAndEntity = registry.process(HttpMethod.GET, MessageFormat.format(ConfiguratorConstants.listFriendsPattern, "u1"), null);
		assertEquals(HttpStatus.SC_OK, statusAndEntity.status);
		assertEquals("fr1_1,fr1_2", EntityUtils.toString(statusAndEntity.entity));

		EasyMock.verify(manager);
	}

	public void testAddFriends() throws Exception {
		manager.add("u1", "fr1");
		EasyMock.replay(manager);

		StatusAndEntity statusAndEntity = registry.process(HttpMethod.POST, MessageFormat.format(ConfiguratorConstants.addDeleteFriendPattern, "u1", "fr1"), null);
		assertEquals(HttpStatus.SC_OK, statusAndEntity.status);
		assertNull(statusAndEntity.entity);

		EasyMock.verify(manager);

	}
	
	public void testDeleteFriends() throws Exception {
		manager.delete("u1", "fr1");
		EasyMock.replay(manager);
		
		StatusAndEntity statusAndEntity = registry.process(HttpMethod.DELETE, MessageFormat.format(ConfiguratorConstants.addDeleteFriendPattern, "u1", "fr1"), null);
		assertEquals(HttpStatus.SC_OK, statusAndEntity.status);
		assertNull(statusAndEntity.entity);
		
		EasyMock.verify(manager);
	}
	
	public void testFriendsUsage() throws Exception{
		EasyMock.expect(manager.friendsUsage("u1")).andReturn(UsageTestData.fr1_a1b3_fr2_b1c2);
		EasyMock.replay(manager);
		
		StatusAndEntity statusAndEntity = registry.process(HttpMethod.GET, MessageFormat.format(ConfiguratorConstants.friendsUsagePattern, "u1"), null);
		assertEquals(HttpStatus.SC_OK, statusAndEntity.status);
		Tests.assertEquals(UsageTestData.fr1_a1b3_fr2_b1c2, persistance.parseFriendsUsage(Strings.unzip(EntityUtils.toByteArray(statusAndEntity.entity))));
		
		EasyMock.verify(manager);
		
	}

	@Override
	protected FriendsConfigurator makeConfigurator() {
		return new FriendsConfigurator(manager = EasyMock.createMock(IFriendAndFriendManager.class), persistance);
	}

}
