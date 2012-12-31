package org.softwarefm.core.friends.internal;

import java.util.List;

import net.miginfocom.swt.MigLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.browser.BrowserAndFriendsComposite;
import org.softwarefm.core.friends.FriendData;
import org.softwarefm.core.friends.IWikiGetterCallback;
import org.softwarefm.core.swt.HasComposite;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.utilities.functions.IFunction1;

/** This is a test class, allows the wiki data getter to be checked */
public class WikiDataGetterComposite extends HasComposite {

	private final BrowserAndFriendsComposite browserAndFriendsComposite;

	public WikiDataGetterComposite(Composite parent, SoftwareFmContainer<?> container, final String user, final String password) {
		super(parent);
		setLayout(new MigLayout("fill", "0[]0", "0[]0[grow]0"));
		Composite rowComposite = Swts.createMigComposite(getComposite(), SWT.NULL, new MigLayout("fill", "", ""), "wrap");
		browserAndFriendsComposite = new BrowserAndFriendsComposite(getComposite(), container);
		browserAndFriendsComposite.setLayoutData("grow");
		final WikiDataGetter wikiDataGetter = new WikiDataGetter(browserAndFriendsComposite, "http://softwarefm.com/{0}");
		final WikiLoginHelperForTests wikiLoginHelper = new WikiLoginHelperForTests(browserAndFriendsComposite);
		Browser.clearSessions();
		Swts.Buttons.makeMigButtonForTest(rowComposite, "Login", new Listener() {
			@Override
			public void handleEvent(Event event) {
				wikiLoginHelper.login(user, password);
			}
		}, "");
		Swts.Buttons.makeMigButtonForTest(rowComposite, "Logout", new Listener() {
			@Override
			public void handleEvent(Event event) {
				wikiLoginHelper.logout();
			}
		}, "");
		Swts.Buttons.makeMigButtonForTest(rowComposite, "Find name", new Listener() {
			@Override
			public void handleEvent(Event event) {
				System.out.println(wikiDataGetter.myName());
			}
		}, "");
		Swts.Buttons.makeMigButtonForTest(rowComposite, "Find friends", new Listener() {
			@Override
			public void handleEvent(Event event) {
				wikiDataGetter.myFriends(new IWikiGetterCallback<List<FriendData>>() {
					@Override
					public void success(List<FriendData> result) {
						System.out.println("Found: " + result);

					}

					@Override
					public void notLoggedIn() {
						System.out.println("Not logged in");
					}

					@Override
					public boolean isValid() {
						return !getComposite().isDisposed();
					}

					@Override
					public void badResponse(int statusCode, String textFromServer) {
						System.err.println("Bad Response " + statusCode);
						System.err.println(textFromServer);

					}
				});
			}
		}, "");

	}

	protected void setUrl(String url) {
		browserAndFriendsComposite.setUrl(url);
	}

	public static void main(String[] args) {
		Swts.Show.display(WikiDataGetterComposite.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				WikiDataGetterComposite wikiDataGetterComposite = new WikiDataGetterComposite(from, SoftwareFmContainer.makeForTests(from.getDisplay()), "Phil", "psr123");
				wikiDataGetterComposite.layout();
				wikiDataGetterComposite.setUrl("www.google.com");
				return wikiDataGetterComposite.getComposite();
			}
		});
	}

}
