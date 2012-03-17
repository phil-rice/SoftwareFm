/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.resources.IResourceGetter;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.card.internal.CardCollectionHolder;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.configuration.internal.BasicCardConfigurator;
import org.softwareFm.swt.details.IDetailsFactoryCallback;
import org.softwareFm.swt.swt.Swts;

public class CardCollectionHolderMain {
	public static void main(String[] args) {
		Swts.Show.displayNoLayout(CardCollectionHolder.class.getSimpleName(), new IFunction1<Composite, Composite>() {
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