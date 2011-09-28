package org.softwareFm.eclipse.fixture;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.util.EntityUtils;
import org.softwareFm.display.browser.IBrowserCallback;
import org.softwareFm.display.browser.IBrowserServiceBuilder;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.ISimpleMap;
import org.softwareFm.utilities.maps.Maps;

public class BrowserService implements IBrowserServiceBuilder, ISimpleMap<String, IFunction1<String, String>> {

	private final ExecutorService service = new ThreadPoolExecutor(2, 10, 2, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
	private final DefaultHttpClient client;
	private final Map<String, IFunction1<String, String>> transformerMap = Maps.newMap();

	public BrowserService() {
		DefaultHttpClient rawClient = new DefaultHttpClient();
		ClientConnectionManager mgr = rawClient.getConnectionManager();
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(mgr.getSchemeRegistry()));
	}

	@Override
	public Future<String> processUrl(final String feedType, final String url, final IBrowserCallback callback) {
		final IFunction1<String, String> transformer = transformerMap.get(feedType);
		if (transformer == null)
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.unrecognisedFeedType, feedType, Lists.sort(transformerMap.keySet())));
		return service.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				HttpGet get = new HttpGet(url.trim());
				HttpResponse httpResponse = client.execute(get);
				String reply = EntityUtils.toString(httpResponse.getEntity());
				String result = transformer.apply(reply);
				callback.process(httpResponse.getStatusLine().getStatusCode(), result);
				return result;
			}
		});
	}

	@Override
	public BrowserService register(String feedType, IFunction1<String, String> feedPostProcessor) {
		if (transformerMap.containsKey(feedType))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "feedType", transformerMap.get(feedType), feedType));
		transformerMap.put(feedType, feedPostProcessor);
		return this;
	}

	@Override
	public IFunction1<String, String> get(String key) {
		IFunction1<String, String> result = transformerMap.get(key);
		if (result == null)
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.unrecognisedFeedType, key, keys()));
		return result;
	}

	@Override
	public List<String> keys() {
		return Lists.sort(transformerMap.keySet());
	}
	public void shutDown() {
		service.shutdown();
	}
}
