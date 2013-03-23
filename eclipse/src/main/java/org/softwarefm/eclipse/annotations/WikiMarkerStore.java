package org.softwarefm.eclipse.annotations;

import org.softwarefm.core.cache.IArtifactDataCache;
import org.softwarefm.core.mediawiki.Wiki;
import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.strings.Strings;

public class WikiMarkerStore implements IMarkerStore {

	private final static int sessionTime = 2 * 60 * 1000;// 2 mins
	private final Wiki wiki;
	private long lastLoggedTime;
	private final IArtifactDataCache artifactDataCache;

	public WikiMarkerStore(IArtifactDataCache artifactDataCache, String host, String apiOffset) {
		this.artifactDataCache = artifactDataCache;
		this.wiki = new Wiki(host, Strings.urlWithSlash(apiOffset));

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
			String existing = artifactDataCache.codeHtml(sfmId);
			if (existing != null) {
				process(markerFoundCallback, existing);
				System.out.println("Found: " + sfmId);
			} else {
				login();
				String text = wiki.getRenderedText("Code:" + sfmId);
				artifactDataCache.putCodeHtml(sfmId, text);
				process(markerFoundCallback, text);
				System.out.println("Went and got: " + sfmId);
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private void process(ICallback<String> markerFoundCallback, String text) throws Exception {
		if (!text.contains(" (page does not exist)")) {
			String title = Strings.findItem(text, "<p>", "</p>");
			markerFoundCallback.process(title);
		}
	}
}
