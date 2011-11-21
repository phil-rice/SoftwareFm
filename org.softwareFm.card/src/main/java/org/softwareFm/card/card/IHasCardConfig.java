package org.softwareFm.card.card;

import org.softwareFm.card.configuration.CardConfig;

/** interface to reduce the data needed to be known about the object being passed in*/
public interface IHasCardConfig {

	CardConfig getCardConfig();
}
