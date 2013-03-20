package org.softwarefm.helloannotations.annotations.internal;

import org.softwarefm.core.mediawiki.Wiki;
import org.softwarefm.helloannotations.annotations.IMarkerStore;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.strings.Strings;

public class WikiMarkerStore implements IMarkerStore {

	private final static int sessionTime = 2 * 60 * 1000;// 2 mins
	private final Wiki wiki;
	private long lastLoggedTime;

	public WikiMarkerStore(String host, String apiOffset) {
		wiki = new Wiki(host, Strings.urlWithSlash(apiOffset));
		wiki.setThrottle(0);
		wiki.setUsingCompressedRequests(false);
	}

	void login() {
		long now = System.currentTimeMillis();
		if (lastLoggedTime == 0 || now > lastLoggedTime + sessionTime)
			try {
				wiki.login("Bot", "botme".toCharArray());// we could optimise this by trying / failing / logging in / trying... or some other strategy
				lastLoggedTime = now;
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
	}

	@Override
	public void makerFor(String sfmId, ICallback<String> markerFoundCallback) {
		try {
			login();
			String text = wiki.getRenderedText("Code:" + sfmId);
			if (!text.contains(" (page does not exist)")){
				String title = Strings.findItem(text, "<p>", "</p>");
				markerFoundCallback.process(title);
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}
}
