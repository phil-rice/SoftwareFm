/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.snippets;

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;
import org.softwareFm.swt.browser.BrowserPart;
import org.softwareFm.swt.browser.IBrowserCompositeBuilder;
import org.softwareFm.swt.browser.IBrowserConfigurator;
import org.softwareFm.swt.browser.IBrowserPart;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.constants.DisplayConstants;
import org.softwareFm.swt.dataStore.ICardDataStore;
import org.softwareFm.swt.dataStore.ICardDataStoreCallback;

import de.java2html.Java2Html;

public class SnippetFeedConfigurator implements IBrowserConfigurator {

	public static void configure(IBrowserCompositeBuilder browser, CardConfig cardConfig) {
		new SnippetFeedConfigurator(cardConfig.cardDataStore, cardConfig.resourceGetterFn).configure(browser);
	}

	private final ICardDataStore cardDataStore;
	private final IFunction1<String, IResourceGetter> resourceGetterFn;

	public SnippetFeedConfigurator(ICardDataStore cardDataStore, IFunction1<String, IResourceGetter>  resourceGetterFn) {
		this.cardDataStore = cardDataStore;
		this.resourceGetterFn = resourceGetterFn;
	}

	@Override
	public void configure(IBrowserCompositeBuilder builder) {
		builder.register(DisplayConstants.snippetFeedType, new IFunction1<Composite, IBrowserPart>() {
			@Override
			public IBrowserPart apply(Composite from) throws Exception {
				return new BrowserPart(from, SWT.NULL) {
					@Override
					public void displayUrl(String url) {
						cardDataStore.processDataFor(url, new ICardDataStoreCallback<Void>() {
							@Override
							public Void process(String url, Map<String, Object> result) throws Exception {
								String template = IResourceGetter.Utils.getOrException(resourceGetterFn, CardConstants.snippet, JarAndPathConstants.snipperTemplateKey);
								String content = Java2Html.convertToHtml(Strings.nullSafeToString(result.get("content")));
								String html = MessageFormat.format(template, //
										Strings.htmlEscape(Strings.nullSafeToString(result.get("title"))), //
										Strings.htmlEscape(Strings.nullSafeToString(result.get("description"))), //
										content);
								displayReply(CommonConstants.okStatusCode, html);
								return null;
							}

							@Override
							public Void noData(String url) throws Exception {
								process(url, Maps.emptyStringObjectMap());
								return null;
							}
						});
						super.displayUrl(url);
					}
				};
			}
		});

	}

}