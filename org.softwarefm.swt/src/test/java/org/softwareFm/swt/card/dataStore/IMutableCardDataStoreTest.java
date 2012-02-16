/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.dataStore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import junit.framework.TestCase;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.future.Futures;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.swt.dataStore.IAfterEditCallback;
import org.softwareFm.swt.dataStore.ICardDataStoreCallback;
import org.softwareFm.swt.dataStore.IMutableCardDataStore;
import org.softwareFm.swt.dataStore.MemoryAfterEditCallback;

public class IMutableCardDataStoreTest extends TestCase {

	private MutableCardDataStoreMock mockWithCollection;
	private MutableCardDataStoreMock mockWithoutCollection;
	private final Map<String, Object> collection = Maps.stringObjectLinkedMap("coll", "ection");
	private final Map<String, Object> data = Maps.stringObjectLinkedMap("a", 1);

	public void testAddCollectionItemToCollectionWhenNoCollectionCreatesCollectionThenCreatesItem() {
		MemoryAfterEditCallback memory = IAfterEditCallback.Utils.memory();
		IMutableCardDataStore.Utils.addCollectionItemToCollection(mockWithoutCollection, "collectionUrl", "collectionName", "itemUrlFragment", data, memory);
		assertEquals(Arrays.asList(//
				"clearCache:collectionUrl", //
				"process:collectionUrl",//
				"put:collectionUrl:{sling:resourceType=collection}",//
				"put:collectionUrl/itemUrlFragment:{a=1}"), //
				mockWithoutCollection.actions);
	}

	public void testAddCollectionItemToCollectionWhenCollectionCreatesItem() {
		MemoryAfterEditCallback memory = IAfterEditCallback.Utils.memory();
		IMutableCardDataStore.Utils.addCollectionItemToCollection(mockWithCollection, "collectionUrl", "collectionName", "itemUrlFragment", data, memory);
		assertEquals(Arrays.asList(//
				"clearCache:collectionUrl", //
				"process:collectionUrl",//
				"put:collectionUrl/itemUrlFragment:{a=1}"), //
				mockWithCollection.actions);
	}

	public void testAddCollectionItemToBaseWhenNoCollectionCreatesCollectionThenCreatesItem() {
		MemoryAfterEditCallback memory = IAfterEditCallback.Utils.memory();
		IMutableCardDataStore.Utils.addCollectionItemToBase(mockWithoutCollection, "baseUrl", "collectionName", "itemUrlFragment", data, memory);
		assertEquals(Arrays.asList(//
				"clearCache:baseUrl/collectionName", //
				"process:baseUrl/collectionName",//
				"put:baseUrl/collectionName:{sling:resourceType=collection}",//
				"put:baseUrl/collectionName/itemUrlFragment:{a=1}"), //
				mockWithoutCollection.actions);
	}

	public void testAddCollectionItemToBaseWhenCollectionCreatesItem() {
		MemoryAfterEditCallback memory = IAfterEditCallback.Utils.memory();
		IMutableCardDataStore.Utils.addCollectionItemToBase(mockWithCollection, "baseUrl", "collectionName", "itemUrlFragment", data, memory);
		assertEquals(Arrays.asList(//
				"clearCache:baseUrl/collectionName", //
				"process:baseUrl/collectionName",//
				"put:baseUrl/collectionName/itemUrlFragment:{a=1}"), //
				mockWithCollection.actions);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mockWithoutCollection = new MutableCardDataStoreMock(null);
		mockWithCollection = new MutableCardDataStoreMock(collection);
	}

}

class MutableCardDataStoreMock implements IMutableCardDataStore {

	final List<String> actions = Lists.newList();
	private final Map<String, Object> processResult;

	public MutableCardDataStoreMock(Map<String, Object> processResult) {
		this.processResult = processResult;
	}

	@Override
	public <T> Future<T> processDataFor(String url, ICardDataStoreCallback<T> callback) {
		try {
			actions.add("process:" + url);
			if (processResult == null)
				callback.noData(url);
			else
				callback.process(url, processResult);
			return Futures.doneFuture(null);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void clearCache(String url) {
		actions.add("clearCache:" + url);
	}

	@Override
	public Future<?> put(String url, Map<String, Object> map, IAfterEditCallback callback) {
		try {
			actions.add("put:" + url + ":" + map);
			callback.afterEdit(url);
			return Futures.doneFuture(null);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void clearCaches() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Future<?> makeRepo(String url, IAfterEditCallback callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(String url, IAfterEditCallback callback) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void refresh(String url) {
		throw new UnsupportedOperationException();
	}

}