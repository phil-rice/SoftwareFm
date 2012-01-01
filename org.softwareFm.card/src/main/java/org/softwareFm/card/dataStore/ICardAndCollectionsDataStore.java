/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.dataStore;

import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.configuration.CardConfig;

/** Gets data about a card, and the may ask for follow up data as well. */
public interface ICardAndCollectionsDataStore {

	/** Note that the cardholder is populated early, but the card may get prodded with valueHasChanged, and the future isn't done until all values have been changed */
	CardAndCollectionsStatus processDataFor(final ICardHolder cardHolder, final CardConfig cardConfig, final String url, ICardAndCollectionDataStoreVisitor whenFinished);
}