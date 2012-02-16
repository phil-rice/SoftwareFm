/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import java.util.concurrent.ExecutionException;

import org.softwareFm.swt.card.ICard;

public class ExplorerAddFromRootWithExistingCollectionsIntegrationTest extends ExplorerAddingCollectionsIntegrationTest{

	@Override
	protected void addMe(String collection, String collectionUrl, String nameInMainCard, int count, String urlFragment, IAddingCallback<ICard> addingCallback) throws Exception, ExecutionException {
		makeCollectionUrlAndExistingUrl(collectionUrl);
		addFromRoot(collection, nameInMainCard, count, urlFragment, addingCallback);
		
	}

}