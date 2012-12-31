package org.softwarefm.core.friends.internal;

import org.eclipse.swt.browser.Browser;
import org.softwarefm.core.browser.BrowserComposite;

public class WikiLoginHelperForTests {
	public final static String loginUrl = "http://data.softwarefm.com/mediawiki/index.php?title=Special:UserLogin&returnto=Main+Page";
	public final static String logoutUrl = "http://data.softwarefm.com/mediawiki/index.php?title=Special:UserLogout&returnto=Main+Page";

	private final BrowserComposite browserComposite;

	public WikiLoginHelperForTests(BrowserComposite browserComposite) {
		this.browserComposite = browserComposite;
	}

	public void login(String user, String password) {
		browserComposite.setUrlAndWait(loginUrl);
		browserComposite.evaluateScript("document.getElementById('wpName1').value='" + user + "'");
		browserComposite.evaluateScript("document.getElementById('wpPassword1').value='" + password + "'");
		browserComposite.evaluateScriptAndWaitForLoad("document.forms['userlogin'].submit()");
	}

	public void logout() {
		browserComposite.setUrlAndWait(logoutUrl);
		Browser.clearSessions();
	}

}
