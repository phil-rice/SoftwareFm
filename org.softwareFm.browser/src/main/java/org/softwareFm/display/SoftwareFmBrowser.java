package org.softwareFm.display;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.util.EntityUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.composites.IHasControl;

public class SoftwareFmBrowser implements IHasControl, IBrowserService {

	private final Composite parent;
	private final Browser browser;
	private final ExecutorService executorService;
	private final DefaultHttpClient client;

	public SoftwareFmBrowser(Composite parent, ExecutorService executorService) {
		this.parent = parent;
		this.executorService = executorService;
		DefaultHttpClient rawClient = new DefaultHttpClient();
		ClientConnectionManager mgr = rawClient.getConnectionManager();
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(mgr.getSchemeRegistry()));
		browser = new Browser(parent, SWT.NULL);

	}

	@Override
	public Control getControl() {
		return browser;
	}


	@Override
	public Future<String> processUrl(final String url, final IFeedCallback callback) {
		return executorService.submit(new Callable<String>() {
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

	public void shutDown() {
		service.shutdown();
	}

}
