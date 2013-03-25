package org.softwarefm.eclipse.annotations;

import org.softwarefm.core.cache.IArtifactDataCache;
import org.softwarefm.core.mediawiki.Wiki;
import org.softwarefm.shared.social.ISocialManager;
import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.strings.Strings;

public class WikiMarkerStore implements IMarkerStore {

	public final static String globalMarker = "org.softwarefm.code.marker.global";
	public final static String myMarker = "org.softwarefm.code.marker.personal";

	private final static int sessionTime = 2 * 60 * 1000;// 2 mins
	private final Wiki wiki;
	private long lastLoggedTime;
	private final IArtifactDataCache artifactDataCache;
	private final ISocialManager socialManager;

	public WikiMarkerStore(IArtifactDataCache artifactDataCache, ISocialManager socialManager, String host, String apiOffset) {
		this.artifactDataCache = artifactDataCache;
		this.socialManager = socialManager;
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
	public void makerFor(String sfmId, IMarkerCallback markerFoundCallback) {
		try {
			String existingGlobal = artifactDataCache.codeHtml(sfmId);
			if (existingGlobal != null) {
				processGlobal(markerFoundCallback, globalMarker, sfmId, existingGlobal);
			} else {
				login();
				String text = wiki.getRenderedText("Code:" + sfmId);
				artifactDataCache.putCodeHtml(sfmId, text);
				processGlobal(markerFoundCallback, globalMarker, sfmId, text);
			}
			String myName = socialManager.myName();
			boolean loggedIn = myName != null && !myName.equals("");
			if (loggedIn) {
				String existingPersonal = artifactDataCache.myCodeHtml(myName, sfmId);
				if (existingPersonal != null) {
					processGlobal(markerFoundCallback, myMarker, sfmId, existingPersonal);
				} else {
					login();
					String text = wiki.getRenderedText("MyCode:" + Strings.url(myName, sfmId));
					artifactDataCache.putMyCodeHtml(myName, sfmId, text);
					processGlobal(markerFoundCallback, myMarker, sfmId, text);
				}
			}
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	private void processGlobal(IMarkerCallback markerFoundCallback, String type, String sfmId, String value) throws Exception {
		if (!value.contains(" (page does not exist)")) {
			String title = Strings.findItem(value, "<p>", "</p>");
			markerFoundCallback.mark(type, sfmId, title);
		}
	}
}
