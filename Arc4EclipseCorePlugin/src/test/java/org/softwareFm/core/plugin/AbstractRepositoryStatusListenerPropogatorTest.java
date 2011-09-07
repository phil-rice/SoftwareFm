package org.softwareFm.core.plugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.softwareFm.displayCore.api.IRepositoryAndUrlGeneratorMapGetter;
import org.softwareFm.displayCore.api.RepositoryStatusListenerPropogator;
import org.softwareFm.repository.api.IRepositoryStatusListener;
import org.softwareFm.repository.api.ISoftwareFmLogger;
import org.softwareFm.repository.api.ISoftwareFmRepository;
import org.softwareFm.repository.api.IUrlGenerator;
import org.softwareFm.repository.api.IUrlGeneratorMap;
import org.softwareFm.repository.api.RepositoryDataItemStatus;
import org.softwareFm.repository.constants.RepositoryConstants;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.maps.Maps;

public abstract class AbstractRepositoryStatusListenerPropogatorTest extends TestCase {

	abstract protected RepositoryStatusListenerPropogator getPropogator();

	private final AtomicInteger urlGeneratorCount = new AtomicInteger();
	private final AtomicInteger getDataCount = new AtomicInteger();
	private final AtomicInteger notifyCount = new AtomicInteger();

	private final AtomicReference<String> entityRef = new AtomicReference<String>();
	private final AtomicReference<String> urlRef = new AtomicReference<String>();
	private final AtomicReference<Map<String, Object>> contextRef = new AtomicReference<Map<String, Object>>();
	private RepositoryStatusListenerPropogator propogator;
	private String originalEntity;
	private String dependantEntity;
	private String urlKey;

	public void testListenerPropogatesWhenEverythingOk() throws Exception {
		String dependantUrl = "dependantUrl";

		Map<String, Object> context = ISoftwareFmRepository.Utils.makePrimaryContext(originalEntity);
		Map<String, Object> item = Maps.makeMap(RepositoryConstants.entity, originalEntity, urlKey, dependantUrl);
		propogator.statusChanged("someOriginalUrl", RepositoryDataItemStatus.FOUND, item, context);
		assertEquals(1, urlGeneratorCount.get());
		assertEquals(0, notifyCount.get());
		assertEquals(1, getDataCount.get());
		assertEquals(dependantEntity, entityRef.get());
		Map<String, Object> expectedContext = ISoftwareFmRepository.Utils.makeSecondaryContext(dependantEntity, urlKey, dependantUrl);
		assertEquals(expectedContext, contextRef.get());
		assertEquals("/" + dependantEntity + "_s/276/dependanturl", urlRef.get());
	}

	public void testListenerPropogatesWhenThereIsNoData() throws Exception {
		Map<String, Object> item = Maps.makeMap(RepositoryConstants.entity, originalEntity); // note that the urlKey is not here...
		Map<String, Object> context = ISoftwareFmRepository.Utils.makePrimaryContext(originalEntity);

		propogator.statusChanged("someOriginalUrl", RepositoryDataItemStatus.FOUND, item, context);

		assertEquals(1, notifyCount.get());
		assertEquals(0, urlGeneratorCount.get());
		assertEquals(0, getDataCount.get());
		assertEquals(dependantEntity, entityRef.get());
		Map<String, Object> expectedContext = ISoftwareFmRepository.Utils.makeSecondaryNotFoundContext(dependantEntity);
		assertEquals(expectedContext, contextRef.get());

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		propogator = getPropogator();
		propogator.setRepositoryAndUrlGeneratorMapGetter(getGetter());

		originalEntity = propogator.getOriginalEntity();
		dependantEntity = propogator.getDependantEntity();
		urlKey = propogator.getUrlKey();
	}

	protected IRepositoryAndUrlGeneratorMapGetter getGetter() {
		IRepositoryAndUrlGeneratorMapGetter result = new IRepositoryAndUrlGeneratorMapGetter() {

			@Override
			public IUrlGeneratorMap getUrlGeneratorMap() {
				return new IUrlGeneratorMap() {

					@Override
					public IUrlGenerator get(String key) {
						urlGeneratorCount.incrementAndGet();
						return IUrlGenerator.Utils.urlGenerator(key + "_");
					}

					@Override
					public List<String> keys() {
						fail();
						return null;
					}

				};
			}

			@Override
			public ISoftwareFmRepository getRepository() {
				return new ISoftwareFmRepository() {

					@Override
					public Future<?> getData(String entity, String url, Map<String, Object> context) {
						getDataCount.incrementAndGet();
						entityRef.set(entity);
						urlRef.set(url);
						contextRef.set(context);
						return Futures.doneFuture(Maps.makeMap("a", 1));
					}

					@Override
					public Future<?> modifyData(String entity, String url, String name, Object value, Map<String, Object> context) {
						fail();
						return null;
					}

					@Override
					public void addStatusListener(IRepositoryStatusListener listener) {
						fail();
					}

					@Override
					public void removeStatusListener(IRepositoryStatusListener listener) {
						fail();
					}

					@Override
					public void addLogger(ISoftwareFmLogger logger) {
						fail();
					}

					@Override
					public void shutdown() {
						fail();
					}

					@Override
					public void notifyListenersThereIsNoData(String entity, Map<String, Object> context) {
						entityRef.set(entity);
						contextRef.set(context);
						notifyCount.incrementAndGet();

					}
				};
			}
		};
		return result;

	}

}
