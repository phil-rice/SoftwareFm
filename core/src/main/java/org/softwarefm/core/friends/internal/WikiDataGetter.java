package org.softwarefm.core.friends.internal;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwarefm.core.browser.BrowserComposite;
import org.softwarefm.core.friends.IWikiDataGetter;
import org.softwarefm.core.friends.IWikiGetterCallback;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.utilities.strings.Strings;

public class WikiDataGetter implements IWikiDataGetter {

	public final static String UserPagePattern = "http://data.softwarefm.com/wiki/User:{0}";
	public final static String friendsHomePageMarker = "<a href=\"http://data.softwarefm.com/wiki/Special:ToggleUserPage\" rel=\"nofollow\">Use wiki userpage</a>";
	public final static String toggleHomePageUrl = "http://data.softwarefm.com/wiki/Special:ToggleUserPage";
	public final static String startOfFriendContainer = "<div class=\"user-relationship-container\">";
	public final static String endOfFriendsContainer = "<div class=\"cleared\"></div>";

	private final BrowserComposite browserComposite;
	private final String imageUrlMask;

	public WikiDataGetter(BrowserComposite browserComposite, String imageUrlMask) {
		this.browserComposite = browserComposite;
		this.imageUrlMask = imageUrlMask;
	}

	@Override
	public String myName() {
		String html = browserComposite.getHtml();
		String container = Strings.findItem(html, "<div id=\"p-personal", "</div>");
		if (container != null) {
			String name = Strings.findItem(container, "User:", "\"");
			return name;
		}
		return null;
	}

	@Override
	public void myFriends(IWikiGetterCallback<List<FriendData>> callback) {
		String myName = myName();
		if (myName == null)
			callback.notLoggedIn();
		else {
			browserComposite.setUrlAndWait(MessageFormat.format(UserPagePattern, myName));
			if (!browserComposite.getHtml().contains(friendsHomePageMarker))
				browserComposite.setUrlAndWait(toggleHomePageUrl);
			if (!browserComposite.getHtml().contains(friendsHomePageMarker))
				System.out.println("Could not find home page with friends");
			String html = browserComposite.getHtml();
			List<FriendData> result = new ArrayList<FriendData>();
			String container = Strings.findItem(html, startOfFriendContainer, endOfFriendsContainer);
			AtomicInteger index = new AtomicInteger();
			if (container != null)
				while (true) {
					String name = Strings.findItem(container, "User:", "\"", index);
					String image = Strings.findItem(container, " src=\"", "\"", index);
					if (name == null)
						break;
					result.add(new FriendData(name, image == null ? null : MessageFormat.format(imageUrlMask, image)));
				}
			System.out.println("Finished looking");
			callback.success(result);

		}
	}
}
