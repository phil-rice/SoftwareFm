/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.explorer;

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.card.dataStore.ICardDataStoreCallback;
import org.softwareFm.crowdsourced.softwarefm.SoftwareFmConstants;
import org.softwareFm.display.browser.BrowserPart;
import org.softwareFm.display.browser.IBrowserCompositeBuilder;
import org.softwareFm.display.browser.IBrowserConfigurator;
import org.softwareFm.display.browser.IBrowserPart;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.server.constants.CommonConstants;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

import de.java2html.Java2Html;

public class SnippetFeedConfigurator implements IBrowserConfigurator {

	public static void configure(IBrowserCompositeBuilder browser, CardConfig cardConfig) {
		new SnippetFeedConfigurator(cardConfig.cardDataStore, Functions.call(cardConfig.resourceGetterFn, CardConstants.snippet)).configure(browser);
	}

	private final ICardDataStore cardDataStore;
	private final IResourceGetter resourceGetter;

	public SnippetFeedConfigurator(ICardDataStore cardDataStore, IResourceGetter resourceGetter) {
		this.cardDataStore = cardDataStore;
		this.resourceGetter = resourceGetter;
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
								String template = IResourceGetter.Utils.getOrException(resourceGetter, SoftwareFmConstants.snipperTemplateKey);
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