package org.softwareFm.card.internal.details;

import java.util.Arrays;

import org.softwareFm.card.api.IDetailsFactoryCallback;
import org.softwareFm.card.api.LineItem;
import org.softwareFm.card.internal.CardCollectionHolder;
import org.softwareFm.card.internal.ScrollingCardCollectionHolder;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.functions.IFunction1;
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
		IHasControl actual = adder.add(shell, parentCard, cardConfig, lineItem.key, lineItem.value,IDetailsFactoryCallback.Utils.noCallback());
		ScrollingCardCollectionHolder scrollingHolder = (ScrollingCardCollectionHolder) actual;
		CardCollectionHolder holder = scrollingHolder.getCardHolder();
		assertSame(lineItem.key, holder.getKey());
		assertSame(lineItem.value, holder.getValue());
		assertEquals(expectedUrl, holder.getRootUrl());
	}


	protected void noEdittingHappensWithThisDetail() {
	}

	protected IFunction1<LineItem, String> justValue = new IFunction1<LineItem, String>() {
		@Override
		public String apply(LineItem from) throws Exception {
			return Strings.nullSafeToString(from.value);
		}
	};
	protected IFunction1<LineItem, String> addPrefixToValue = new IFunction1<LineItem, String>() {
		@Override
		public String apply(LineItem from) throws Exception {
			return "pre_" + from.value;
		}
	};

}
