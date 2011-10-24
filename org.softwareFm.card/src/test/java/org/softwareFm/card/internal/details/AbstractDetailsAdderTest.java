package org.softwareFm.card.internal.details;

import java.util.Arrays;

import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.internal.CardCollectionHolder;
import org.softwareFm.display.composites.IHasControl;

public abstract class AbstractDetailsAdderTest<T extends IDetailAdder> extends AbstractDetailTest {

	private T adder;
	protected DetailFactory detailFactory;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		adder = makeDetailsAdder();
		detailFactory = new DetailFactory(Arrays.<IDetailAdder> asList(adder));
	}

	abstract protected T makeDetailsAdder();

	protected void checkGetCardCollectionsHolder(KeyValue keyValue, String expectedUrl) {
		IHasControl actual = adder.add(shell, parentCard, cardConfig, keyValue, ICardSelectedListener.Utils.noListener());
		CardCollectionHolder holder = (CardCollectionHolder) actual;
		assertSame(cardConfig, holder.getCardConfig());
		assertSame(keyValue, holder.getKeyValue());
		assertEquals(expectedUrl, holder.getRootUrl());
	}

}
