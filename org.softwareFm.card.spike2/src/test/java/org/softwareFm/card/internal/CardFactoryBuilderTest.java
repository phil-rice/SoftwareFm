package org.softwareFm.card.internal;

import junit.framework.TestCase;

public class CardFactoryBuilderTest extends TestCase{

	public void testConstructor() {
		CardFactoryBuilder builder = new CardFactoryBuilder();
		assertNotNull(builder.build());
	}

}
