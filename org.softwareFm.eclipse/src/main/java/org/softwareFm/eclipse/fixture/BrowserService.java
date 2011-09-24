package org.softwareFm.eclipse.fixture;

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
import org.softwareFm.display.IFeedCallback;
import org.softwareFm.display.IBrowserService;

public class BrowserService implements IBrowserService {

	private final ExecutorService service = new ThreadPoolExecutor(2, 10, 2, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
	private final DefaultHttpClient client;

	public BrowserService() {
		DefaultHttpClient rawClient = new DefaultHttpClient();
		ClientConnectionManager mgr = rawClient.getConnectionManager();
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(mgr.getSchemeRegistry()));
	}

	@Override
	public Future<String> processRssUrl(final String url, final IFeedCallback callback) {
		return service.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				HttpGet get = new HttpGet(url);
				HttpResponse httpResponse = client.execute(get);
				String result = EntityUtils.toString(httpResponse.getEntity());
				callback.process(httpResponse.getStatusLine().getStatusCode(), result);
				return result;
			}
		});
	}

	public void shutDown() {
		service.shutdown();
	}

}
