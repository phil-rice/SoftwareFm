package org.softwareFm.display.timeline;

import java.util.List;
import java.util.concurrent.Future;

import org.softwareFm.display.browser.IBrowser;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.future.Futures;

public class BrowserMock implements IBrowser{

	public final  List<PlayItem> playItems = Lists.newList();
	
	@Override
	public Future<String> processUrl(String feedType, String url) {
		playItems.add(new PlayItem(feedType, url));
		return Futures.doneFuture(null);
	}

}
