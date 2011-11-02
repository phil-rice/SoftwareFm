package org.softwareFm.card.internal.details;

import java.util.Arrays;

import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.internal.CardCollectionHolder;
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

	protected void checkGetCardCollectionsHolder(KeyValue keyValue, String expectedUrl) {
		IHasControl actual = adder.add(shell, parentCard, cardConfig, keyValue.key, keyValue.value,IDetailsFactoryCallback.Utils.noCallback());
		CardCollectionHolder holder = (CardCollectionHolder) actual;
		assertSame(cardConfig, holder.getCardConfig());
		assertSame(keyValue.key, holder.getKey());
		assertSame(keyValue.value, holder.getValue());
		assertEquals(expectedUrl, holder.getRootUrl());
	}

	abstract public void testAfterEditHappensAfterCardDataStoreUpdated();

	protected void noEdittingHappensWithThisDetail() {
		
	}

	protected IFunction1<KeyValue, String> justValue = new IFunction1<KeyValue, String>() {
		@Override
		public String apply(KeyValue from) throws Exception {
			return Strings.nullSafeToString(from.value);
		}
	};
	protected IFunction1<KeyValue, String> addPrefixToValue = new IFunction1<KeyValue, String>() {
		@Override
		public String apply(KeyValue from) throws Exception {
			return "pre_" + from.value;
		}
	};

}
