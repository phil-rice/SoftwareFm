/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.dataStore;

import java.util.Map;

import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.dataStore.ICardAndCollectionDataStoreVisitor;

public class CardAndCollectionDataStoreVisitorMonitored implements ICardAndCollectionDataStoreVisitor {

	public int initialUrlCount;
	public int initialCardCount;
	public int requestingFollowUpCount;
	public int followedUpCount;
	public int noDataCount;
	public int finishedCount;

	@Override
	public void initialUrl(ICardHolder cardHolder, CardConfig cardConfig, String url) {
		initialUrlCount++;

	}

	@Override
	public void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, ICard card) {
		initialCardCount++;
	}

	@Override
	public void requestingFollowup(ICardHolder cardHolder, String url, ICard card, String followOnUrlFragment) {
		requestingFollowUpCount++;
	}

	@Override
	public void followedUp(ICardHolder cardHolder, String url, ICard card, String followUpUrl, Map<String, Object> result) {
		followedUpCount++;
	}

	@Override
	public void noData(ICardHolder cardHolder, String url, ICard card, String followUpUrl) {
		noDataCount++;
	}

	@Override
	public void finished(ICardHolder cardHolder, String url, ICard card) {
		finishedCount++;
	}

}