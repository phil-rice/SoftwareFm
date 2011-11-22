package org.softwareFm.card.card;


public abstract class AbstractLineItemFunction <T>implements ILineItemFunction <T>{
	protected String findKey(LineItem from) {
		String key = from.key.replace(':', '_');
		return key;
	}

}
