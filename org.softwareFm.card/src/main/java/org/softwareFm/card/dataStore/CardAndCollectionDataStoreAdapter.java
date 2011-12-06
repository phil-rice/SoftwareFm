/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.dataStore;

import java.util.Map;

import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.configuration.CardConfig;

/** An implementation with empty methods of {@link ICardAndCollectionDataStoreVisitor}, allowing an implementer to only override the methods they are interested in */
public class CardAndCollectionDataStoreAdapter implements ICardAndCollectionDataStoreVisitor {

	@Override
	public void initialUrl(ICardHolder cardHolder, CardConfig cardConfig, String url) {
	}

	@Override
	public void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, ICard card) {
	}

	@Override
	public void requestingFollowup(ICardHolder cardHolder, String url, ICard card, String followOnUrlFragment) {
	}

	@Override
	public void followedUp(ICardHolder cardHolder, String url, ICard card, String followUpUrl, Map<String, Object> result) {
	}

	@Override
	public void noData(ICardHolder cardHolder, String url, ICard card, String followUpUrl) {
	}

	@Override
	public void finished(ICardHolder cardHolder, String url, ICard card) throws Exception {
	}

}