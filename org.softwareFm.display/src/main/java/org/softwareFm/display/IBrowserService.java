package org.softwareFm.display;

import java.util.concurrent.Future;


public interface IBrowserService {

	Future<String> processUrl(String url, IFeedCallback callback);


}
