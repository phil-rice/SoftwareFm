package org.softwareFm.card.internal.details;


public class CollectionItemDetailAdderTest extends AbstractDetailsAdderTest<CollectionItemDetailAdder> {
	public void testWithNonListGetNull() {
		checkGetNull(detailFactory, stringValue);
		checkGetNull(detailFactory, intValue);
	}

	public void testNeedMoreThanItBeingAMap() {
		checkGetNull(detailFactory, mapValue);
		checkGetNull(detailFactory, folderValue);
		checkGetNull(detailFactory, collectionValue);
	}

//	public void testMakesACardHolderIfHasTypeAndIsntCollection() {
//		OneCardHolder actual = (OneCardHolder) adder.add(shell, parentCard, cardConfig, typedValueNotCollection.key, typedValueNotCollection.value,IDetailsFactoryCallback.Utils.noCallback());
//		assertSame(cardConfig, actual.getCardConfig());
//		CardHolder cardHolder = actual.getCardHolder();
//		assertNull(cardHolder.getCard());
//		assertEquals(parentCard.url() + "/key", cardHolder.getCard().url());
//	}

	@Override
	protected CollectionItemDetailAdder makeDetailsAdder() {
		return new CollectionItemDetailAdder();
	}

}
