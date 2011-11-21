package org.softwareFm.card.card;

import java.util.List;

import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardChangedListener;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.utilities.collections.Lists;

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
