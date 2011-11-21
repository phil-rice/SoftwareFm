package org.softwareFm.card.card.internal.details;

import org.softwareFm.card.details.internal.CollectionsDetailAdder;


public class CollectionsDetailAdderTest extends AbstractDetailsAdderTest<CollectionsDetailAdder> {

	public void testWithNonListGetNull() {
		checkGetNull(detailFactory, stringValue);
		checkGetNull(detailFactory, intValue);
	}

	public void testNeedMoreThanItBeingAList() {
		checkGetNull(detailFactory, mapValue);
		checkGetNull(detailFactory, folderValue);
		checkGetNull(detailFactory, typedValueNotCollection);
	}

	public void testNeedsCollectionToMakeACardHolder() {
		checkGetCardCollectionsHolder(collectionValue, parentCard.url()+"/key");
	}

	@Override
	protected CollectionsDetailAdder makeDetailsAdder() {
		return new CollectionsDetailAdder();
	}

}
