package org.softwarefm.core.browser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.friends.internal.WikiLoginHelperForTests;
import org.softwarefm.core.tests.SwtTest;
import org.softwarefm.shared.constants.CommonConstants;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.social.ISocialManager;
import org.softwarefm.shared.usage.IUsagePersistance;
import org.softwarefm.shared.usage.UsageStatData;
import org.softwarefm.shared.usage.UsageTestData;
import org.softwarefm.utilities.events.IMultipleListenerList;
import org.softwarefm.utilities.maps.ISimpleMap;
import org.softwarefm.utilities.maps.SimpleMaps;
import org.softwarefm.utilities.resources.IResourceGetter;
import org.softwarefm.utilities.strings.Strings;

public class BrowserAndFriendsCompositeTest extends SwtTest {
	int friendCount = 3;

	private static final ISimpleMap<FriendData, UsageStatData> friend1_1Usage = SimpleMaps.<FriendData, UsageStatData> makeMap(UsageTestData.friend1, new UsageStatData(1));
	private BrowserAndFriendsComposite browserAndFriendsComposite;
	private ISocialManager socialManager;
	private WikiLoginHelperForTests wikiLoginHelperForTests;

	public void testPutsFriendsIntoImages() {
		browserAndFriendsComposite.setFriendData(friend1_1Usage);
		ToolBar friendsToolBar = browserAndFriendsComposite.friendsToolBar;
		assertEquals(1, friendsToolBar.getItemCount());
		ToolItem friendItem1 = friendsToolBar.getItem(0);
		assertEquals(UsageTestData.friend1.name + ": 1", friendItem1.getToolTipText());
	}

	public void testClickingOnImageGoesToThatPersonsPage() {
		browserAndFriendsComposite.setFriendData(friend1_1Usage);
		final ToolItem friendItem1 = browserAndFriendsComposite.friendsToolBar.getItem(0);
		browserAndFriendsComposite.executeAndWaitToLoad(new Runnable() {
			public void run() {
				friendItem1.notifyListeners(SWT.Selection, new Event());
			}
		});
		assertEquals("http://"+Strings.url(CommonConstants.softwareFmHost, "wiki/User:" +Strings.upperCaseFirstCharacter(UsageTestData.friend1.name)), browserAndFriendsComposite.browser.getUrl());

	}

	public void testDoesntAddFriendsBackInWhenNotLoggedIn() {
		login();
		gotoMyUserPage();
		assertEquals(friendCount, socialManager.myFriends().size());

		logout();
		assertEquals(0, socialManager.myFriends().size());
		assertEquals(null, socialManager.myName());

		gotoUserPage("Phil");
		assertEquals(0, socialManager.myFriends().size());
		assertEquals(null, socialManager.myName());
	}

	public void testFindsFriendsFromUserPage() throws InterruptedException {
		login();
		assertEquals("Phil", socialManager.myName());
		assertEquals(Arrays.asList(), socialManager.myFriends());

		gotoMyUserPage();
		assertEquals("Phil", socialManager.myName());
		assertEquals(friendCount, socialManager.myFriends().size());
	}

	public void testDoesntFindFriendsFromOtherPeoplesUserPage() throws InterruptedException {
		login();
		assertEquals("Phil", socialManager.myName());
		assertEquals(Arrays.asList(), socialManager.myFriends());

		gotoMyUserPage();
		assertEquals("Phil", socialManager.myName());
		List<FriendData> friends = new ArrayList<FriendData>(socialManager.myFriends());
		assertEquals(friendCount, friends.size());
		gotoUserPage("Root");

		assertEquals("Phil", socialManager.myName());
		assertEquals(friends, socialManager.myFriends());
	}

	private void gotoMyUserPage() {
		String name = socialManager.myName();
		assertNotNull(name);
		gotoUserPage(name);
	}

	public void testFindsNameFromPage() {
		login();
		assertEquals("Phil", socialManager.myName());
	}

	public void testFindsNullIfLoggedOut() {
		logout();
		assertNull(socialManager.myName());
	}

	public void testFindsNullIfLoggedOutAfterLoggedIn() {
		logout();
		assertNull(socialManager.myName());

		login();
		assertEquals("Phil", socialManager.myName());

		logout();
		assertNull(socialManager.myName());
	}

	public void testClearsFriendsWhenLogOut() {
		login();
		assertEquals("Phil", socialManager.myName());
		assertEquals(Arrays.asList(), socialManager.myFriends());

		gotoMyUserPage();
		assertEquals("Phil", socialManager.myName());
		assertEquals(friendCount, socialManager.myFriends().size());

		logout();
		assertEquals(null, socialManager.myName());
		assertEquals(0, socialManager.myFriends().size());
	}

	private void gotoUserPage(String name) {
		browserAndFriendsComposite.setUrlAndWait(Strings.url(CommonConstants.softwareFmHost, "wiki/User:" + name));
	}

	private void gotoMainPage() {
		browserAndFriendsComposite.setUrlAndWait(Strings.url(CommonConstants.softwareFmHost, "wiki/MainPage"));
	}

	public void login() {
		wikiLoginHelperForTests.login("Phil", "password");
	}

	public void logout() {
		wikiLoginHelperForTests.logout();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		socialManager = ISocialManager.Utils.socialManager(IMultipleListenerList.Utils.defaultList(), IUsagePersistance.Utils.persistance());
		IResourceGetter resourceGetter = IResourceGetter.Utils.noResources();
		Browser.clearSessions();
		SoftwareFmContainer<Object> container = SoftwareFmContainer.makeForTests(display, resourceGetter, socialManager);
		browserAndFriendsComposite = new BrowserAndFriendsComposite(shell, container);
		wikiLoginHelperForTests = new WikiLoginHelperForTests(browserAndFriendsComposite);
	}

}
