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
import org.softwarefm.shared.usage.IUsageStats;
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
		addListeners();
		replay();
		assertEquals(null, socialManager.myName());
		assertEquals(Collections.emptyList(), socialManager.myFriends());
	}

	public void testSettingNameFiresListeners() {
		addListeners();
		foundNameListener1.foundName("someName");
		foundNameListener2.foundName("someName");
		replay();

		socialManager.setMyName("someName");
		assertEquals("someName", socialManager.myName());
	}

	public void testSettingNameDoesntFiresListenerIfSameValue() {
		addListeners();
		foundNameListener1.foundName("someName");
		foundNameListener2.foundName("someName");
		replay();

		socialManager.setMyName("someName");
		socialManager.setMyName("someName");
		socialManager.setMyName("someName");
		assertEquals("someName", socialManager.myName());
	}

	public void testSettingNameFiresListenerIfDifferentValue() {
		addListeners();
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
		addListeners();
		foundFriendsListener1.foundFriends(friends1);
		foundFriendsListener2.foundFriends(friends1);
		replay();
		socialManager.setFriendsData(friends1);
		assertEquals(friends1, socialManager.myFriends());
	}

	public void testSettingFriendsDoesntFiresListenersIfSameValue() {
		addListeners();
		foundFriendsListener1.foundFriends(friends1);
		foundFriendsListener2.foundFriends(friends1);
		replay();
		socialManager.setFriendsData(friends1);
		socialManager.setFriendsData(friends1);
		socialManager.setFriendsData(friends1);
		assertEquals(friends1, socialManager.myFriends());
	}

	public void testSettingFriendsFiresListenerIfDifferentValue() {
		addListeners();
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
		String saved = socialManager.serialize();

		SocialManager socialManager2 = new SocialManager(IMultipleListenerList.Utils.defaultList(), IUsagePersistance.Utils.persistance());
		socialManager2.populate(saved);

		assertEquals(null, socialManager2.myName());
		assertEquals(Collections.emptyList(), socialManager2.myFriends());
		assertEquals(Collections.emptyList(), socialManager2.names());
	}

	public void testGetUsageStatsForCode() {
		replay();
		socialManager.setUsageData("n1", UsageTestData.statsa1b3);
		socialManager.setUsageData("n2", UsageTestData.statsa2b1);

		assertEquals(new UsageStatData(1), socialManager.getUsageStatsForCode("n1", "a"));
		assertEquals(new UsageStatData(3), socialManager.getUsageStatsForCode("n1", "b"));
		assertEquals(new UsageStatData(0), socialManager.getUsageStatsForCode("notIn", "a"));
	}

	public void testGetUsageStatsForArtifact() {
		replay();
		socialManager.setUsageData("n1", IUsageStats.Utils.from(//
				"artifact:path1/version1", new UsageStatData(2),//
				"artifact:path1/version2", new UsageStatData(3),//
				"artifact:path2/version1", new UsageStatData(5),//
				"artifact:path2/version2", new UsageStatData(7)));

		assertEquals(new UsageStatData(5), socialManager.getUsageStatsForArtifact("n1", "artifact:path1"));
		assertEquals(new UsageStatData(12), socialManager.getUsageStatsForArtifact("n1", "artifact:path2"));
	}

	public void testGetCodeUsageStatsForFriends() {
		replay();
		socialManager.setUsageData("fr1", UsageTestData.statsa1b3);
		socialManager.setUsageData("fr2", UsageTestData.statsb1c2);
		socialManager.setUsageData("not_fr", UsageTestData.statsa2b6);
		socialManager.setFriendsData(UsageTestData.friends);

		Tests.assertMapEquals(socialManager.getFriendsCodeUsage("a"), UsageTestData.friend1, new UsageStatData(1), UsageTestData.friend2, new UsageStatData(0));
		Tests.assertMapEquals(socialManager.getFriendsCodeUsage("b"), UsageTestData.friend1, new UsageStatData(3), UsageTestData.friend2, new UsageStatData(1));

	}

	public void testGetArtifactUsageStatsForFriends() {
		replay();
		socialManager.setUsageData("fr1", IUsageStats.Utils.from(//
				"artifact:path1/version1", new UsageStatData(2),//
				"artifact:path1/version2", new UsageStatData(3),//
				"artifact:path2/version1", new UsageStatData(5),//
				"artifact:path2/version2", new UsageStatData(7)));
		socialManager.setUsageData("fr2", IUsageStats.Utils.from(//
				"artifact:path2/version1", new UsageStatData(11),//
				"artifact:path2/version2", new UsageStatData(13),//
				"artifact:path3/version1", new UsageStatData(17),//
				"artifact:path3/version2", new UsageStatData(19)));

		socialManager.setFriendsData(UsageTestData.friends);

		Tests.assertMapEquals(socialManager.getFriendsArtifactUsage("artifact:path1"), UsageTestData.friend1, new UsageStatData(5), UsageTestData.friend2, new UsageStatData(0));
		Tests.assertMapEquals(socialManager.getFriendsArtifactUsage("artifact:path2"), UsageTestData.friend1, new UsageStatData(12), UsageTestData.friend2, new UsageStatData(24));
	}

	public void testIgnoresNullNames() {
		replay();
		socialManager.setUsageData(null, UsageTestData.statsa1b3);
		assertEquals(0, socialManager.names().size());
		assertEquals(0, socialManager.getUsageStats(null).size());
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
	}

	private void addListeners() {
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
