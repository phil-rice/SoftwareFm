package org.softwarefm.core.browser;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import net.miginfocom.swt.MigLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwarefm.core.SoftwareFmContainer;
import org.softwarefm.core.friends.internal.WikiLoginHelperForTests;
import org.softwarefm.core.swt.HasComposite;
import org.softwarefm.core.swt.Swts;
import org.softwarefm.shared.constants.CommonConstants;
import org.softwarefm.shared.social.FriendData;
import org.softwarefm.shared.social.IFoundFriendsListener;
import org.softwarefm.shared.social.IFoundNameListener;
import org.softwarefm.utilities.functions.IFunction1;
import org.softwarefm.utilities.strings.Strings;

/** This is a test class, allows the wiki data getter to be checked */
public class BrowserAndFriendsPersonUnit extends HasComposite {

	private final BrowserAndFriendsComposite browserAndFriendsComposite;
	protected List<FriendData> friendData;

	public BrowserAndFriendsPersonUnit(Composite parent, SoftwareFmContainer<?> container, final String... userAndPasswords) {
		super(parent);
		setLayout(new MigLayout("fill", "0[]0", "0[]0[grow]0"));
		Composite rowComposite = Swts.createMigComposite(getComposite(), SWT.NULL, new MigLayout("fill", "", ""), "wrap");

		browserAndFriendsComposite = new BrowserAndFriendsComposite(getComposite(), container);
		browserAndFriendsComposite.setLayoutData("grow");
		final WikiLoginHelperForTests wikiLoginHelper = new WikiLoginHelperForTests(browserAndFriendsComposite);
		Browser.clearSessions();
		for (int i = 0; i < userAndPasswords.length; i += 2)
			makeLoginButton(rowComposite, wikiLoginHelper, i, userAndPasswords);
		Swts.Buttons.makeMigButtonForTest(rowComposite, "Logout", new Listener() {
			@Override
			public void handleEvent(Event event) {
				wikiLoginHelper.logout();
			}
		}, "");
		final AtomicReference<String> lastName = new AtomicReference<String>();
		Swts.Buttons.makeMigButtonForTest(rowComposite, "User Page", new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (lastName.get() == null)
					setUrl(Strings.url(CommonConstants.softwareFmHost, "wiki/User:NotLoggedIn"));
				else
					setUrl(Strings.url(CommonConstants.softwareFmHost, "wiki/User:" + lastName.get()));
			}
		}, "");
		Swts.Buttons.makeMigButtonForTest(rowComposite, "Path String", new Listener() {

			@Override
			public void handleEvent(Event event) {
				browserAndFriendsComposite.setUrl(Strings.url(CommonConstants.softwareFmHost, "wiki/Code:Java.lang/String"));
			}
		}, "");
		container.socialManager.addFoundFriendsListener(new IFoundFriendsListener() {
			@Override
			public void foundFriends(List<FriendData> friends) {
				System.out.println("Found friends listener: " + friends);
			}
		});
		container.socialManager.addFoundNameListener(new IFoundNameListener() {
			@Override
			public void foundName(String name) {
				System.out.println("Found name listener: " + name);
				lastName.set(name);
			}
		});
	}

	private void makeLoginButton(Composite rowComposite, final WikiLoginHelperForTests wikiLoginHelper, final int i, final String... userAndPasswords) {
		Swts.Buttons.makeMigButtonForTest(rowComposite, "Login " + userAndPasswords[i + 0], new Listener() {
			@Override
			public void handleEvent(Event event) {
				wikiLoginHelper.login(userAndPasswords[i + 0], userAndPasswords[i + 1]);
			}
		}, "");
	}

	protected void setUrl(String url) {
		browserAndFriendsComposite.setUrl(url);
	}

	public static void main(String[] args) {
		Swts.Show.display(BrowserAndFriendsPersonUnit.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				BrowserAndFriendsPersonUnit browserAndFriendsPersonUnit = new BrowserAndFriendsPersonUnit(from, SoftwareFmContainer.makeForTests(from.getDisplay()), //
						"Root", "iwtbde",//
						"Phil", "psr123");
				browserAndFriendsPersonUnit.layout();
				browserAndFriendsPersonUnit.setUrl("www.google.com");
				return browserAndFriendsPersonUnit.getComposite();
			}
		});
	}

}
