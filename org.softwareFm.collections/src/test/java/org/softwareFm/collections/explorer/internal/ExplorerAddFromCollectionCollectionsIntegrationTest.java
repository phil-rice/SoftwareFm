package org.softwareFm.collections.explorer.internal;

import java.util.concurrent.ExecutionException;

import org.softwareFm.card.card.ICard;

public class ExplorerAddFromCollectionCollectionsIntegrationTest extends ExplorerAddingCollectionsIntegrationTest{

	@Override
	protected void addMe(String collection, String collectionUrl, String nameInMainCard, int count, String urlFragment, IAddingCallback<ICard> addingCallback) throws Exception, ExecutionException {
		addFromCollection(collection, nameInMainCard, count, urlFragment, addingCallback, collectionUrl);
	}

}