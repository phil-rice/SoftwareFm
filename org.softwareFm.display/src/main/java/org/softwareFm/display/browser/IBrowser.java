package org.softwareFm.display.browser;

import java.util.concurrent.Future;

public interface IBrowser {
	Future<String> processUrl(String feedType, String url);

}
