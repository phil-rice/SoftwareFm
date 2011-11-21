package org.softwareFm.card.card.internal;

import org.softwareFm.card.card.ILineItemFunction;
import org.softwareFm.card.card.LineItem;

public abstract class AbstractLineItemToStringFunction <T>implements ILineItemFunction <T>{
	protected String findKey(LineItem from) {
		String key = from.key.replace(':', '_');
		return key;
	}

}
