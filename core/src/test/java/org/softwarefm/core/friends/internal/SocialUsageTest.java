package org.softwarefm.core.friends.internal;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.softwarefm.core.friends.ISocialUsageListener;
import org.softwarefm.core.jdtBinding.ArtifactData;
import org.softwarefm.core.jdtBinding.CodeData;
import org.softwarefm.core.selection.ISelectedBindingManager;
import org.softwarefm.core.selection.SelectedBindingAdapter;
import org.softwarefm.core.url.IUrlStrategy;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.social.ISocialManager;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.IUsageStats;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.shared.usage.UsageTestData;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.maps.SimpleMaps;

/** Note this only tests the listener */
public class SocialUsageTest extends TestCase {

	private SocialUsage socialUsage;
	private ISocialManager socialManager;
	private SelectedBindingAdapter socialUsageListener;

	private ISocialUsageListener listener1;
	private ISocialUsageListener listener2;

	public void testCodeSelectionCausesCodeUsage() {
		ISimpleMap<FriendData, UsageStatData> friendsCodeUsage = SimpleMaps.<FriendData, UsageStatData> makeMap(//
				UsageTestData.friend1, new UsageStatData(7), //
				UsageTestData.friend2, new UsageStatData(0));
		listener1.codeUsage("code:pack/class1", new UsageStatData(2), friendsCodeUsage);
		listener2.codeUsage("code:pack/class1", new UsageStatData(2), friendsCodeUsage);
		EasyMock.replay(listener1, listener2);
		socialUsageListener.codeSelectionOccured(new CodeData("pack", "class1"), 0);
	}

	public void testNotInAJarCausesNoArtifactUsage() {
		listener1.noArtifactUsage();
		listener2.noArtifactUsage();
		EasyMock.replay(listener1, listener2);
		socialUsageListener.notInAJar(null, 0);
	}

	public void testUnknownDigestCausesNoArtifactUsage() {
		listener1.noArtifactUsage();
		listener2.noArtifactUsage();
		EasyMock.replay(listener1, listener2);
		socialUsageListener.unknownDigest(null, 0);
	}

	public void testProjectCausesArtifactUsage() {
		ISimpleMap<FriendData, UsageStatData> friendsCodeUsage = SimpleMaps.<FriendData, UsageStatData> makeMap(//
				UsageTestData.friend1, new UsageStatData(24), //
				UsageTestData.friend2, new UsageStatData(19));
		listener1.artifactUsage("artifact:group/artifact", new UsageStatData(8), friendsCodeUsage);
		listener2.artifactUsage("artifact:group/artifact", new UsageStatData(8), friendsCodeUsage);
		EasyMock.replay(listener1, listener2);
		socialUsageListener.artifactDetermined(new ArtifactData(null, "group", "artifact", "v1"), 0);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IMultipleListenerList listenerList = IMultipleListenerList.Utils.defaultList();
		socialManager = ISocialManager.Utils.socialManager(listenerList, IUsagePersistance.Utils.persistance());
		socialUsage = new SocialUsage(listenerList, IUrlStrategy.Utils.urlStrategy(), ISelectedBindingManager.Utils.noManager(), socialManager);
		socialUsageListener = socialUsage.makeListener();
		listener1 = EasyMock.createMock(ISocialUsageListener.class);
		listener2 = EasyMock.createMock(ISocialUsageListener.class);

		socialUsage.addSocialUsageListener(listener1);
		 socialUsage.addSocialUsageListener(listener2);

		socialManager.setMyName("me");
		socialManager.setFriendsData(UsageTestData.friends);
		socialManager.setUsageData("me", IUsageStats.Utils.from(//
				"code:pack/class1", new UsageStatData(2),//
				"artifact:group/artifact/v1", new UsageStatData(3),//
				"artifact:group/artifact/v2", new UsageStatData(5)));
		socialManager.setUsageData(UsageTestData.friend1.name, IUsageStats.Utils.from(//
				"code:pack/class1", new UsageStatData(7),//
				"artifact:group/artifact/v1", new UsageStatData(11), //
				"artifact:group/artifact/v2", new UsageStatData(13)));
		socialManager.setUsageData(UsageTestData.friend2.name, IUsageStats.Utils.from(//
				"code:pack/class2", new UsageStatData(17),//
				"artifact:group/artifact/v1", new UsageStatData(19)));

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		EasyMock.verify(listener1, listener2);
	}

}
