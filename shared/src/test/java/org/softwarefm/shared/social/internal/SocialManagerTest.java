package org.softwarefm.shared.social.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.social.IFoundFriendsListener;
import org.softwarefm.shared.social.IFoundNameListener;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.shared.usage.UsageTestData;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.maps.SimpleMaps;
import org.softwarefm.utilities.tests.Tests;

public class SocialManagerTest extends TestCase {
	private final static List<FriendData> friends1 = Arrays.asList(new FriendData("one", null), new FriendData("two", "two.jpg"));
	private final static List<FriendData> friends2 = Arrays.asList(new FriendData("one", null));

	private IFoundFriendsListener foundFriendsListener1;
	private IFoundFriendsListener foundFriendsListener2;
	private IFoundNameListener foundNameListener1;
	private IFoundNameListener foundNameListener2;
	private SocialManager socialManager;

	public void testInitialSettings() {
		replay();
		assertEquals(null, socialManager.myName());
		assertEquals(Collections.emptyList(), socialManager.myFriends());
	}

	public void testSettingNameFiresListeners() {
		foundNameListener1.foundName("someName");
		foundNameListener2.foundName("someName");
		replay();

		socialManager.setMyName("someName");
		assertEquals("someName", socialManager.myName());
	}

	public void testSettingNameDoesntFiresListenerIfSameValue() {
		foundNameListener1.foundName("someName");
		foundNameListener2.foundName("someName");
		replay();

		socialManager.setMyName("someName");
		socialManager.setMyName("someName");
		socialManager.setMyName("someName");
		assertEquals("someName", socialManager.myName());
	}

	public void testSettingNameFiresListenerIfDifferentValue() {
		foundNameListener1.foundName("someName");
		foundNameListener2.foundName("someName");
		foundNameListener1.foundName("someOtherName");
		foundNameListener2.foundName("someOtherName");
		foundNameListener1.foundName("someName");
		foundNameListener2.foundName("someName");
		replay();

		socialManager.setMyName("someName");
		socialManager.setMyName("someOtherName");
		socialManager.setMyName("someName");
		assertEquals("someName", socialManager.myName());
	}

	public void testSettingFriendsFiresListeners() {
		foundFriendsListener1.foundFriends(friends1);
		foundFriendsListener2.foundFriends(friends1);
		replay();
		socialManager.setFriendsData(friends1);
		assertEquals(friends1, socialManager.myFriends());
	}

	public void testSettingFriendsDoesntFiresListenersIfSameValue() {
		foundFriendsListener1.foundFriends(friends1);
		foundFriendsListener2.foundFriends(friends1);
		replay();
		socialManager.setFriendsData(friends1);
		socialManager.setFriendsData(friends1);
		socialManager.setFriendsData(friends1);
		assertEquals(friends1, socialManager.myFriends());
	}

	public void testSettingFriendsFiresListenerIfDifferentValue() {
		foundFriendsListener1.foundFriends(friends1);
		foundFriendsListener2.foundFriends(friends1);
		foundFriendsListener1.foundFriends(friends2);
		foundFriendsListener2.foundFriends(friends2);
		foundFriendsListener1.foundFriends(friends1);
		foundFriendsListener2.foundFriends(friends1);
		replay();
		socialManager.setFriendsData(friends1);
		socialManager.setFriendsData(friends1);
		socialManager.setFriendsData(friends2);
		socialManager.setFriendsData(friends2);
		socialManager.setFriendsData(friends1);
		socialManager.setFriendsData(friends1);
		assertEquals(friends1, socialManager.myFriends());
	}

	public void testStatsAccess() {
		replay();
		Tests.assertEquals(SimpleMaps.<String, UsageStatData> empty(), socialManager.getUsageStats("n1"));
		Tests.assertEquals(SimpleMaps.<String, UsageStatData> empty(), socialManager.getUsageStats("n2"));

		socialManager.setUsageData("n1", UsageTestData.statsa1b3);
		socialManager.setUsageData("n2", UsageTestData.statsa2b1);

		Tests.assertEquals(UsageTestData.statsa1b3, socialManager.getUsageStats("n1"));
		Tests.assertEquals(UsageTestData.statsa2b1, socialManager.getUsageStats("n2"));

		socialManager.clearUsageData();

		Tests.assertEquals(SimpleMaps.<String, UsageStatData> empty(), socialManager.getUsageStats("n1"));
		Tests.assertEquals(SimpleMaps.<String, UsageStatData> empty(), socialManager.getUsageStats("n2"));

		socialManager.setUsageData("n1", UsageTestData.statsa1b3);
		socialManager.setUsageData("n2", UsageTestData.statsa2b1);

		Tests.assertEquals(UsageTestData.statsa1b3, socialManager.getUsageStats("n1"));
		Tests.assertEquals(UsageTestData.statsa2b1, socialManager.getUsageStats("n2"));
	}

	public void testSerialize() {
		replay();
		removeListeners();
		socialManager.setUsageData("n1", UsageTestData.statsa1b3);
		socialManager.setUsageData("n2", UsageTestData.statsa2b1);
		socialManager.setMyName("someName");
		socialManager.setFriendsData(friends1);
		String saved = socialManager.serialize();

		SocialManager socialManager2 = new SocialManager(IMultipleListenerList.Utils.defaultList(), IUsagePersistance.Utils.persistance());
		socialManager2.populate(saved);
		assertEquals("someName", socialManager2.myName());
		assertEquals(friends1, socialManager2.myFriends());
		assertEquals(UsageTestData.statsa1b3, socialManager2.getUsageStats("n1"));
		assertEquals(UsageTestData.statsa2b1, socialManager2.getUsageStats("n2"));
		Tests.assertEqualsAsSet(Arrays.asList("n1", "n2"), socialManager2.names());
	}

	public void testSerializeWithNullNameAndEmptyData() {
		replay();
		removeListeners();
		String saved = socialManager.serialize();

		SocialManager socialManager2 = new SocialManager(IMultipleListenerList.Utils.defaultList(), IUsagePersistance.Utils.persistance());
		socialManager2.populate(saved);

		assertEquals(null, socialManager2.myName());
		assertEquals(Collections.emptyList(), socialManager2.myFriends());
		assertEquals(Collections.emptyList(), socialManager2.names());
	}

	private void removeListeners() {
		socialManager.removeFoundFriendsListener(foundFriendsListener1);
		socialManager.removeFoundFriendsListener(foundFriendsListener2);
		socialManager.removeFoundNameListener(foundNameListener1);
		socialManager.removeFoundNameListener(foundNameListener2);
	}

	private void replay() {
		EasyMock.replay(foundFriendsListener1, foundFriendsListener2, foundNameListener1, foundNameListener2);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		foundFriendsListener1 = EasyMock.createMock(IFoundFriendsListener.class);
		foundFriendsListener2 = EasyMock.createMock(IFoundFriendsListener.class);
		foundNameListener1 = EasyMock.createMock(IFoundNameListener.class);
		foundNameListener2 = EasyMock.createMock(IFoundNameListener.class);
		socialManager = new SocialManager(IMultipleListenerList.Utils.defaultList(), IUsagePersistance.Utils.persistance());
		socialManager.addFoundFriendsListener(foundFriendsListener1);
		socialManager.addFoundFriendsListener(foundFriendsListener2);
		socialManager.addFoundNameListener(foundNameListener1);
		socialManager.addFoundNameListener(foundNameListener2);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		EasyMock.verify(foundFriendsListener1, foundFriendsListener2, foundNameListener1, foundNameListener2);
	}
}
