package org.softwareFm.display;

import java.util.concurrent.Future;


public interface IBrowserService {

	Future<String> processRssUrl(String url, IFeedCallback callback);


}
