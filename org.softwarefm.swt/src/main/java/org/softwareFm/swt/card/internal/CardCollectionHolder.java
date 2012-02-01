/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.internal;

import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.Urls;
import org.softwareFm.swt.card.IHasKeyAndValue;
import org.softwareFm.swt.card.dataStore.CardDataStoreFixture;
import org.softwareFm.swt.composites.IHasComposite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.internal.BasicCardConfigurator;
import org.softwareFm.swt.details.IDetailsFactoryCallback;
import org.softwareFm.swt.details.internal.IGotDataCallback;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.swt.Swts.Show;

public class CardCollectionHolder implements IHasComposite {

	String rootUrl;
	private final CardCollectionHolderComposite content;

	public static class CardCollectionHolderComposite extends HoldsCardHolder implements IHasKeyAndValue {

		private String key;
		private Object value;

		public CardCollectionHolderComposite(Composite parent, CardConfig cardConfig) {
			super(parent, SWT.NULL, cardConfig);
		}

		@SuppressWarnings("unchecked")
		protected Future<?> setKeyValue(final String rootUrl, String key, Object value, final IGotDataCallback callback) {
			if (isDisposed())
				return null;
			this.key = key;
			this.value = value;
			Swts.removeAllChildren(this);
			if (value instanceof Map<?, ?>) {
				Map<String, ?> map = (Map<String, ?>) value;
				Map<String, ?> sortedMap = Maps.sortByKey(map, Strings.compareVersionNumbers());
				for (final Map.Entry<String, ?> childEntry : sortedMap.entrySet()) {
					if (childEntry.getValue() instanceof Map<?, ?>) {
						String detailUrl = Urls.composeWithSlash(rootUrl, key, childEntry.getKey());
						String title = childEntry.getKey();
						makeCardHolder(detailUrl, title);
					}
				}
			}
			callback.gotData(CardCollectionHolderComposite.this);
			return null;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public Object getValue() {
			return value;
		}

	}

	public CardCollectionHolder(Composite parent, CardConfig cardConfig) {
		content = new CardCollectionHolderComposite(parent, cardConfig);
	}

	public void setKeyValue(final String rootUrl, String key, Object value, IDetailsFactoryCallback callback) {
		this.rootUrl = rootUrl;
		content.setKeyValue(rootUrl, key, value, callback);
		content.addCardSelectedListener(callback);
	}

	public String getRootUrl() {
		return rootUrl;
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public CardConfig getCardConfig() {
		return content.cardConfig;
	}

	public String getKey() {
		return content.key;
	}

	public Object getValue() {
		return content.value;
	}

	public static void main(String[] args) {
		Show.displayNoLayout(CardCollectionHolder.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				Display display = from.getDisplay();
				final CardConfig cardConfig = new BasicCardConfigurator().configure(display, CardDataStoreFixture.syncCardConfig(display));
				IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, null, "navBar.prev.title");
				final CardCollectionHolder cardCollectionHolder = new CardCollectionHolder(from, cardConfig);
				cardCollectionHolder.setKeyValue(CardDataStoreFixture.url, "stuff", Maps.makeMap(CardDataStoreFixture.dataIndexedByUrlFragment), IDetailsFactoryCallback.Utils.resizeAfterGotData());
				return cardCollectionHolder.getComposite();
			}
		});
	}
}