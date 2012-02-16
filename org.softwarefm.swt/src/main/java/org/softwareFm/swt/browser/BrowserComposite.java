/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.browser;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.util.EntityUtils;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.future.Futures;
import org.softwareFm.common.maps.ISimpleMap;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.swt.constants.DisplayConstants;
import org.softwareFm.swt.swt.Swts;

public class BrowserComposite implements IBrowserCompositeBuilder, ISimpleMap<String, IBrowserPart> {

	private final IServiceExecutor service;
	private final DefaultHttpClient client;
	private final Map<String, IBrowserPart> transformerMap = Maps.newMap();
	private final Composite content;
	private final StackLayout stackLayout;

	public BrowserComposite(Composite parent, int style, IServiceExecutor service) {
		this.service = service;
		this.content = Swts.newComposite(parent, style, "BrowserComposite");
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
					try {
						transformer.displayReply(200, "");
						transformer.displayUrl(url);
						makeOnlyVisible(transformer);
					} catch (Exception e) {
						throw WrappedException.wrap(e);
					}
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
							try {
								transformer.displayReply(httpResponse.getStatusLine().getStatusCode(), reply);
								makeOnlyVisible(transformer);
							} catch (Exception e) {
								throw WrappedException.wrap(e);
							}
						}
					});
					return reply;
				}
			});
	}

	private void makeOnlyVisible(final IBrowserPart transformer) {
		stackLayout.topControl = transformer.getControl();
		content.layout();
		// Swts.layoutDump(content);
	}

	@Override
	public IBrowserPart register(String feedType, IFunction1<Composite, IBrowserPart> feedPostProcessor) {
		if (transformerMap.containsKey(feedType))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "feedType", transformerMap.get(feedType), feedType));
		try {
			IBrowserPart part = feedPostProcessor.apply(content);
			transformerMap.put(feedType, part);
			content.layout();
			content.redraw();
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

	@Override
	public Control getControl() {
		return content;
	}

	public Control getStackLayoutTopControlForTests() {
		return stackLayout.topControl;
	}

	@Override
	public Composite getComposite() {
		return content;
	}
}