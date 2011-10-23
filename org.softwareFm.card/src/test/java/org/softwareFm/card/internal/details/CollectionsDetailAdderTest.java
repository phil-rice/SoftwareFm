package org.softwareFm.card.internal.details;

import static org.junit.Assert.*;

import org.junit.Test;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.internal.CardCollectionHolder;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.SwtIntegrationTest;

public class CollectionsDetailAdderTest extends
		AbstractDetailsAdderTest<CollectionsDetailAdder> {

	public void testWithNonListGetNull() {
		checkGetNull(detailFactory, stringValue);
		checkGetNull(detailFactory, intValue);
	}

	public void testNeedMoreThanItBeingAList() {
		checkGetNull(detailFactory, listValue);
		checkGetNull(detailFactory, folderValue);
	}

	public void testNeedsCollectionToMakeACardHolder() {
		checkGetCardCollectionsHolder(collectionValue, parentCard.url()+"/"+collectionValue.key);
	}

	@Override
	protected CollectionsDetailAdder makeDetailsAdder() {
		return new CollectionsDetailAdder();
	}

}
