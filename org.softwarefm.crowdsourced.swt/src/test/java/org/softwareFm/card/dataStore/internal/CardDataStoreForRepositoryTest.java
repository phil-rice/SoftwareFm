package org.softwareFm.card.dataStore.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.card.dataStore.IAfterEditCallback;
import org.softwareFm.card.dataStore.MemoryAfterEditCallback;
import org.softwareFm.display.swt.SwtTest;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.future.Futures;

public class CardDataStoreForRepositoryTest extends SwtTest {

	private RepositoryFacardMock facard;
	private CardDataStoreForRepository cardDataStore;

	public void testDelete() {
		MemoryAfterEditCallback memory = IAfterEditCallback.Utils.memory();
		cardDataStore.delete("someUrl", memory);
		assertEquals("someUrl", Lists.getOnly(memory.urls));
		assertEquals("someUrl", Lists.getOnly(facard.deleteUrls));
	}

	public void testClearCache() {
		cardDataStore.clearCache("someUrl");
		assertEquals(0, facard.clearCacheCount.get());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		facard = new RepositoryFacardMock();
		cardDataStore = new CardDataStoreForRepository(shell, facard);
	}

}

class RepositoryFacardMock implements IRepositoryFacard {
	AtomicInteger clearCacheCount = new AtomicInteger();
	final List<String> clearCacheUrls = Lists.newList();
	final List<String> deleteUrls = Lists.newList();

	@Override
	public Future<?> get(String url, IRepositoryFacardCallback callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearCache(String url) {
		clearCacheUrls.add(url);
	}

	@Override
	public void clearCaches() {
		clearCacheCount.incrementAndGet();
	}

	@Override
	public void addHeader(String name, String value) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Future<?> delete(String url, IResponseCallback callback) {
		deleteUrls.add(url);
		callback.process(IResponse.Utils.okText(url, ""));
		return Futures.doneFuture(null);
	}

	@Override
	public Future<?> post(String url, Map<String, Object> map, IResponseCallback callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Future<?> makeRoot(String url, IResponseCallback callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void shutdown() {
		throw new UnsupportedOperationException();
	}

}
