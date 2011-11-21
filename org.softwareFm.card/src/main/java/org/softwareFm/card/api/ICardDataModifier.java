package org.softwareFm.card.api;

import java.util.Map;

/** There are a chain of these modifiers (typically ending with the sorter). Each is responsible for aggregating / enriching / hiding / sorting... data about the card */
public interface ICardDataModifier {

	/** The result can be the rawData is no changes have been made. The rawData should be treated as immutable */
	public Map<String,Object> modify(CardConfig cardConfig, String url, Map<String,Object> rawData);

}
