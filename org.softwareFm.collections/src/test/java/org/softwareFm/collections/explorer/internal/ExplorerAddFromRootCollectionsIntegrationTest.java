package org.softwareFm.collections.explorer.internal;

import org.softwareFm.card.card.ICard;

public class ExplorerAddFromRootCollectionsIntegrationTest extends ExplorerAddingCollectionsIntegrationTest {

	@Override
	protected void addMe(String collection, String collectionUrl, String nameInMainCard, int count, String urlFragment, IAddingCallback<ICard> addingCallback) {
		addFromRoot(collection, nameInMainCard, count, urlFragment, addingCallback);
	}

}
