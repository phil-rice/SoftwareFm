package org.softwareFm.card.card;

import org.softwareFm.card.configuration.CardConfig;

public interface ICardHolderForTests extends ICardHolder{
	String getRootUrl();

	CardConfig getCardConfig();
}
