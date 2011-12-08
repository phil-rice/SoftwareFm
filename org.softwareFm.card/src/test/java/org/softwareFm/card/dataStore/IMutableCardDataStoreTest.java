package org.softwareFm.card.dataStore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import junit.framework.TestCase;

import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.maps.Maps;

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
	
	public void testAddCollectionItemToBaseWhenNoCollectionCreatesCollectionThenCreatesItem(){
		MemoryAfterEditCallback memory = IAfterEditCallback.Utils.memory();
		IMutableCardDataStore.Utils.addCollectionItemToBase(mockWithoutCollection, "baseUrl", "collectionName", "itemUrlFragment", data, memory);
		assertEquals(Arrays.asList(//
				"clearCache:baseUrl/collectionName", //
				"process:baseUrl/collectionName",//
				"put:baseUrl/collectionName:{sling:resourceType=collection}",//
				"put:baseUrl/collectionName/itemUrlFragment:{a=1}"), //
				mockWithoutCollection.actions);
	}

	public void testAddCollectionItemToBaseWhenCollectionCreatesItem(){
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

}