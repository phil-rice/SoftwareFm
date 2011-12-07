package org.softwareFm.collections.explorer;

import java.util.Map;

import org.softwareFm.card.card.ICardData;

public interface IAddCardCallback{

	void ok(ICardData cardData);

	void cancel(ICardData cardData);

	boolean canOk(Map<String,Object> data);
}
