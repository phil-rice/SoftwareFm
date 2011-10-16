package org.softwareFm.card.api;

import org.softwareFm.card.internal.CardFactoryBuilder;

public interface ICardFactoryBuilder {

	ICardFactory build();
	
	static class Utils{
		public static ICardFactoryBuilder builder(){
			return new CardFactoryBuilder();
		}
	}
}
