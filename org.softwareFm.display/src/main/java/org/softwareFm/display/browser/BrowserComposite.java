package org.softwareFm.display.browser;

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
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.maps.ISimpleMap;
import org.softwareFm.utilities.maps.Maps;

public class BrowserComposite implements IBrowserCompositeBuilder, ISimpleMap<String, IBrowserPart> {

	private final ExecutorService service = new ThreadPoolExecutor(2, 10, 2, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
	private final DefaultHttpClient client;
	private final Map<String, IBrowserPart> transformerMap = Maps.newMap();
	private final Composite content;
	private final StackLayout stackLayout;

	public BrowserComposite(Composite parent, int style) {
		this.content = new Composite(parent, style);
		stackLayout = new StackLayout();
		content.setLayout(stackLayout);
		DefaultHttpClient rawClient = new DefaultHttpClient();
		ClientConnectionManager mgr = rawClient.getConnectionManager();
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(mgr.getSchemeRegistry()));
	}

	@Override
	public Future<String> processUrl(String feedType, final String url) {
		final IBrowserPart transformer = transformerMap.get(feedType);
		if (transformer == null)
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.unrecognisedFeedType, feedType, Lists.sort(transformerMap.keySet())));
		if (transformer.usesUrl()) {
			Swts.asyncExec(transformer, new Runnable() {
				@Override
				public void run() {
					transformer.displayUrl(url);
					makeOnlyVisible(transformer);
				}

			});

			return Futures.doneFuture(null);
		} else
			return service.submit(new Callable<String>() {
				@Override
				public String call() throws Exception {
					HttpGet get = new HttpGet(url.trim());
					final HttpResponse httpResponse = client.execute(get);
					final String reply = EntityUtils.toString(httpResponse.getEntity());
					Swts.asyncExec(transformer, new Runnable() {
						@Override
						public void run() {
							transformer.displayReply(httpResponse.getStatusLine().getStatusCode(), reply);
							makeOnlyVisible(transformer);
						}
					});
					return reply;
				}
			});
	}

	private void makeOnlyVisible(final IBrowserPart transformer) {
		stackLayout.topControl = transformer.getControl();
		content.layout();
//		Swts.layoutDump(content);
	}

	@Override
	public IBrowserPart register(String feedType, IFunction1<Composite, IBrowserPart> feedPostProcessor) {
		if (transformerMap.containsKey(feedType))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "feedType", transformerMap.get(feedType), feedType));
		try {
			IBrowserPart part = feedPostProcessor.apply(content);
			transformerMap.put(feedType, part);
			return part;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public IBrowserPart get(String key) {
		IBrowserPart result = transformerMap.get(key);
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

	@Override
	public Control getControl() {
		return content;
	}
}
