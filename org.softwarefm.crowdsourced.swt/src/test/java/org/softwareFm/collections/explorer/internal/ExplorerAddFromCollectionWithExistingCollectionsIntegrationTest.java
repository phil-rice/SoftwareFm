package org.softwareFm.collections.explorer.internal;

import java.util.concurrent.ExecutionException;

import org.softwareFm.card.card.ICard;

public class ExplorerAddFromCollectionWithExistingCollectionsIntegrationTest extends ExplorerAddingCollectionsIntegrationTest {

	@Override
	protected void addMe(String collection, String collectionUrl, String nameInMainCard, int count, String urlFragment, IAddingCallback<ICard> addingCallback) throws Exception, ExecutionException {
		makeCollectionUrlAndExistingUrl(collectionUrl);
		addFromCollection(collection, nameInMainCard, count, urlFragment, addingCallback, collectionUrl);
	}

}
