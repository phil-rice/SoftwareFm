package org.softwareFm.eclipse.usage.internal;

import org.softwareFm.swt.card.ICard;

public class ExplorerAddFromRootCollectionsIntegrationTest extends ExplorerAddingCollectionsIntegrationTest {

	@Override
	protected void addMe(String collection, String collectionUrl, String nameInMainCard, int count, String urlFragment, IAddingCallback<ICard> addingCallback) {
		addFromRoot(collection, nameInMainCard, count, urlFragment, addingCallback);
	}

}
