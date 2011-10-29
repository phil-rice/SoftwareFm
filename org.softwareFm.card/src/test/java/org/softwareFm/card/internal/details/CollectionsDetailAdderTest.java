package org.softwareFm.card.internal.details;


public class CollectionsDetailAdderTest extends AbstractDetailsAdderTest<CollectionsDetailAdder> {

	public void testWithNonListGetNull() {
		checkGetNull(detailFactory, stringValue);
		checkGetNull(detailFactory, intValue);
	}

	public void testNeedMoreThanItBeingAList() {
		checkGetNull(detailFactory, listValue);
		checkGetNull(detailFactory, folderValue);
	}

	public void testNeedsCollectionToMakeACardHolder() {
		checkGetCardCollectionsHolder(collectionValue, parentCard.url() + "/" + collectionValue.key);
	}

	@Override
	protected CollectionsDetailAdder makeDetailsAdder() {
		return new CollectionsDetailAdder();
	}

	@Override
	public void testAfterEditHappensAfterCardDataStoreUpdated() {
		noEdittingHappensWithThisDetail();
	}

}
