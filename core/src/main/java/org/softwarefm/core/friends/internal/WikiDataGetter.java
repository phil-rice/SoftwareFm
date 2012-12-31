package org.softwarefm.core.friends.internal;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.softwarefm.core.browser.BrowserComposite;
import org.softwarefm.core.friends.FriendData;
import org.softwarefm.core.friends.IWikiDataGetter;
import org.softwarefm.core.friends.IWikiGetterCallback;
import org.softwarefm.utilities.jdom.Jdoms;
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
		int start = html.indexOf("<div id=\"p-personal");
		if (start >= 0) {
			int end = html.indexOf("</div>", start) + 6;
			String div = html.substring(start, end);
			for (Element li : Jdoms.findElementsWith(div, "li")) {
				if ("pt-userpage".equals(li.getAttributeValue("id"))) {
					Element a = Jdoms.findOnlyChildTag(li, "a");
					return a.getText();
				}
			}
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
			int start = 0;
			List<FriendData> result = new ArrayList<FriendData>();
			while (true) {
				start = html.indexOf(startOfFriendContainer, start);
				if (start > 0) {
					System.out.println("Start: " + start);
					int end = Strings.indexAfter(html, endOfFriendsContainer, start);
					System.out.println("End of container: " + end);
					if (end >= 0) {
						String container = html.substring(start, end);
						String name = Strings.findItem(container, "User:", "\"");
						String image = Strings.findItem(container, " src=\"", "\"");
						if (name != null && image != null)
							result.add(new FriendData(name, MessageFormat.format(imageUrlMask, image)));
						start = end;
						continue;
					}
				}
				System.out.println("Finished looking");
				callback.success(result);
				return;
			}

		}
	}

}
