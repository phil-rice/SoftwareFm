package org.softwareFm.card.internal;

import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardFactoryBuilder;

public class CardFactoryBuilder implements ICardFactoryBuilder{

	@Override
	public ICardFactory build() {
		return new CardFactory();
	}

}
