package org.softwareFm.card.card.internal.details;

import java.util.Arrays;

import org.softwareFm.card.card.ILineItemFunction;
import org.softwareFm.card.card.LineItem;
import org.softwareFm.card.card.internal.CardCollectionHolder;
import org.softwareFm.card.card.internal.ScrollingCardCollectionHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.details.IDetailAdder;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.card.details.internal.DetailFactory;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.strings.Strings;

public abstract class AbstractDetailsAdderTest<T extends IDetailAdder> extends AbstractDetailTest {

	protected T adder;
	protected DetailFactory detailFactory;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		adder = makeDetailsAdder();
		detailFactory = new DetailFactory(Arrays.<IDetailAdder> asList(adder));
	}

	abstract protected T makeDetailsAdder();

	protected void checkGetCardCollectionsHolder(LineItem lineItem, String expectedUrl) {
		IHasControl actual = adder.add(shell, parentCard, cardConfig, lineItem.key, lineItem.value, IDetailsFactoryCallback.Utils.noCallback());
		ScrollingCardCollectionHolder scrollingHolder = (ScrollingCardCollectionHolder) actual;
		CardCollectionHolder holder = scrollingHolder.getCardHolder();
		assertSame(lineItem.key, holder.getKey());
		assertSame(lineItem.value, holder.getValue());
		assertEquals(expectedUrl, holder.getRootUrl());
	}

	protected void noEdittingHappensWithThisDetail() {
	}

	protected ILineItemFunction<String> justValue = new ILineItemFunction<String>() {
		@Override
		public String apply(CardConfig cardConfig, LineItem from) {
			return Strings.nullSafeToString(from.value);
		}
	};
	protected ILineItemFunction<String> addPrefixToValue = new ILineItemFunction<String>() {
		@Override
		public String apply(CardConfig cardConfig, LineItem from){
			return "pre_" + from.value;
		}
	};

}
