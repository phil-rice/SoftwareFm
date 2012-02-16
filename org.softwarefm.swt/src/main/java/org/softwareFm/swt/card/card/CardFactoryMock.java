/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.card;

import java.util.Map;

import org.softwareFm.swt.card.CardMock;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardFactory;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.configuration.CardConfig;

public class CardFactoryMock implements ICardFactory {

	public int count;

	@Override
	public ICard makeCard(ICardHolder cardHolder, CardConfig cardConfig, String url, Map<String, Object> map) {
		count++;
		CardMock card = new CardMock(cardHolder, cardConfig, url, map);
		cardHolder.setCard(card);
		return card;
	}

}