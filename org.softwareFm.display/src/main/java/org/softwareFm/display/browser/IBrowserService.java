package org.softwareFm.display.browser;

import java.util.concurrent.Future;

public interface IBrowserService {


	Future<String> processUrl(String feedType, String url, IBrowserCallback callback);

}
