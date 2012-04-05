/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.dataStore.internal;

import java.util.Map;

import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.transaction.ITransaction;
import org.softwareFm.swt.ISwtFunction1;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.dataStore.ICardAndCollectionDataStoreVisitor;
import org.softwareFm.swt.dataStore.ICardAndCollectionsDataStore;

/** Note that the callback is called early, but the card may get prodded with valueHasChanged, and the future isn't done until all values have been changed */
public class CardCollectionsDataStore implements ICardAndCollectionsDataStore {

	@Override
	public ITransaction<ICard> processDataFor(IContainer container, final ICardHolder cardHolder, final CardConfig cardConfig, final String url, final ICardAndCollectionDataStoreVisitor visitor) {
		ITransaction<ICard> mainTransaction = container.accessWithCallbackFn(IGitReader.class, new IFunction1<IGitReader, Map<String, Object>>() {
			@Override
			public Map<String, Object> apply(IGitReader reader) throws Exception {
				visitor.initialUrl(cardHolder, cardConfig, url);
				IFileDescription fileDescription = IFileDescription.Utils.plain(url);
				Map<String, Object> data = reader.getFileAndDescendants2(fileDescription);
				return data;
			}

			@Override
			public String toString() {
				return CardCollectionsDataStore.this.getClass().getSimpleName() + ".processDataFor(" + url + ")";
			}
		}, new ISwtFunction1<Map<String, Object>, ICard>() {
			@Override
			public ICard apply(Map<String, Object> from) throws Exception {
				ICard card = cardConfig.cardFactory.makeCard(cardHolder, cardConfig, url, from);
				if (card != null) {
					visitor.initialCard(cardHolder, cardConfig, url, card);
					visitor.finished(cardHolder, url, card);
				}
				return card;
			}

			@Override
			public String toString() {
				return "makeCard";
			}
		});
		return mainTransaction;
	}
}