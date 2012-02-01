/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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
import org.softwareFm.common.strings.Strings;
import org.softwareFm.display.composites.IHasControl;

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
		public String apply(CardConfig cardConfig, LineItem from) {
			return "pre_" + from.value;
		}
	};

}