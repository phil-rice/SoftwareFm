/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.browser;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.ISimpleMap;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.swt.ISwtFunction1;
import org.softwareFm.swt.constants.DisplayConstants;
import org.softwareFm.swt.swt.Swts;

public class BrowserComposite implements IBrowserCompositeBuilder, ISimpleMap<String, IBrowserPart> {

	private final Map<String, IBrowserPart> transformerMap = Maps.newMap();
	private final Composite content;
	private final StackLayout stackLayout;
	private final IContainer container;

	public BrowserComposite(Composite parent, int style, IContainer container) {
		this.container = container;
		this.content = Swts.newComposite(parent, style, "BrowserComposite");
		stackLayout = new StackLayout();
		content.setLayout(stackLayout);
	

	}

	@Override
	public ITransaction<String> processUrl(final String feedType, final String url) {
		final IBrowserPart transformer = transformerMap.get(feedType);
		if (transformer == null)
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.unrecognisedFeedType, feedType, Lists.sort(transformerMap.keySet())));
		final String trimmedUrl = url.trim();
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
			return ITransaction.Utils.doneTransaction(null);
		} else
			return container.accessWithCallbackFn(HttpClient.class, new IFunction1<HttpClient, HttpResponse>() {
				@Override
				public HttpResponse apply(HttpClient client) throws Exception {
					HttpResponse response = client.execute(new HttpGet(trimmedUrl));
					return response;
					
				}
			}, new ISwtFunction1<HttpResponse, String>() {
				@Override
				public String apply(HttpResponse response) throws Exception {
					String string = EntityUtils.toString(response.getEntity());
					int statusCode = response.getStatusLine().getStatusCode();
					transformer.displayReply(statusCode, string);
					makeOnlyVisible(transformer);
					return string;
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