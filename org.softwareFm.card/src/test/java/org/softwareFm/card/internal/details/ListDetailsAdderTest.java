package org.softwareFm.card.internal.details;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;

public class ListDetailsAdderTest extends AbstractDetailsAdderTest {

	public void testWithNonListGetNull() {
		checkGetNull(detailFactory, stringValue);
		checkGetNull(detailFactory, intValue);
	}

	public void testWithListAdderWithListGetCardCollectionsHolder() {
		checkGetCardCollectionsHolder(listValue, parentCard.url());
		checkGetCardCollectionsHolder(collectionValue, parentCard.url());
		checkGetCardCollectionsHolder(folderValue, parentCard.url());
	}

	@Override
	protected IDetailAdder makeDetailsAdder() {
		return new ListDetailAdder();
	}
}
