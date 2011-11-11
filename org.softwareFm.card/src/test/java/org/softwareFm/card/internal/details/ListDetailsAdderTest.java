package org.softwareFm.card.internal.details;


public class ListDetailsAdderTest extends AbstractDetailsAdderTest<ListDetailAdder> {

	public void testWithNonListGetNull() {
		checkGetNull(detailFactory, stringValue);
		checkGetNull(detailFactory, intValue);
	}

	public void testWithListAdderWithListGetCardCollectionsHolder() {
		checkGetCardCollectionsHolder(mapValue, parentCard.url());
		checkGetCardCollectionsHolder(collectionValue, parentCard.url());
		checkGetCardCollectionsHolder(folderValue, parentCard.url());
		checkGetCardCollectionsHolder( typedValueNotCollection, parentCard.url());
	}

	@Override
	protected ListDetailAdder makeDetailsAdder() {
		return new ListDetailAdder();
	}
}
