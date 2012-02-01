/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card;

import java.util.List;

import org.softwareFm.common.collections.Lists;

public class CardChangedListenerMock implements ICardChangedListener {

	public final List<ICard> valueChangedCards = Lists.newList();
	public final List<String> keys = Lists.newList();
	public final List<Object> newValues = Lists.newList();
	public final List<ICardHolder> cardHolders = Lists.newList();
	public final List<ICard> cardChangedCards = Lists.newList();

	@Override
	public void valueChanged(ICard card, String key, Object newValue) {
		valueChangedCards.add(card);
		keys.add(key);
		newValues.add(newValue);
	}

	@Override
	public void cardChanged(ICardHolder cardHolder, ICard card) {
		cardHolders.add(cardHolder);
		cardChangedCards.add(card);
	}

}