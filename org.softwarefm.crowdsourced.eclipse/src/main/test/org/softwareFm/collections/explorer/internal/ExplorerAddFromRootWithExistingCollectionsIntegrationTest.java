package org.softwareFm.collections.explorer.internal;

import java.util.concurrent.ExecutionException;

import org.softwareFm.card.card.ICard;

public class ExplorerAddFromRootWithExistingCollectionsIntegrationTest extends ExplorerAddingCollectionsIntegrationTest{

	@Override
	protected void addMe(String collection, String collectionUrl, String nameInMainCard, int count, String urlFragment, IAddingCallback<ICard> addingCallback) throws Exception, ExecutionException {
		makeCollectionUrlAndExistingUrl(collectionUrl);
		addFromRoot(collection, nameInMainCard, count, urlFragment, addingCallback);
		
	}

}
