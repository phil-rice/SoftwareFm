package org.arc4eclipse.core.plugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseLogger;
import org.arc4eclipse.arc4eclipseRepository.api.IArc4EclipseRepository;
import org.arc4eclipse.arc4eclipseRepository.api.IRepositoryStatusListener;
import org.arc4eclipse.arc4eclipseRepository.api.IUrlGenerator;
import org.arc4eclipse.arc4eclipseRepository.api.IUrlGeneratorMap;
import org.arc4eclipse.arc4eclipseRepository.api.RepositoryDataItemStatus;
import org.arc4eclipse.arc4eclipseRepository.constants.RepositoryConstants;
import org.arc4eclipse.displayCore.api.IRepositoryAndUrlGeneratorMapGetter;
import org.arc4eclipse.displayCore.api.RepositoryStatusListenerPropogator;
import org.arc4eclipse.utilities.future.Futures;
import org.arc4eclipse.utilities.maps.Maps;

public abstract class AbstractRepositoryStatusListenerPropogatorTest extends TestCase {

	abstract protected RepositoryStatusListenerPropogator getPropogator();

	private final AtomicInteger urlGeneratorCount = new AtomicInteger();
	private final AtomicInteger getDataCount = new AtomicInteger();
	private final AtomicReference<String> entityRef = new AtomicReference<String>();
	private final AtomicReference<String> urlRef = new AtomicReference<String>();
	private final AtomicReference<Map<String, Object>> contextRef = new AtomicReference<Map<String, Object>>();

	public void testListenerPropogatesWhenEverythingOk() throws Exception {
		RepositoryStatusListenerPropogator propogator = getPropogator();
		propogator.setRepositoryAndUrlGeneratorMapGetter(getGetter());

		String originalEntity = propogator.getOriginalEntity();
		String dependantEntity = propogator.getDependantEntity();
		String urlKey = propogator.getUrlKey();
		String dependantUrl = "dependantUrl";

		Map<String, Object> context = IArc4EclipseRepository.Utils.makePrimaryContext(originalEntity);
		Map<String, Object> item = Maps.makeMap(RepositoryConstants.entity, originalEntity, urlKey, dependantUrl);
		propogator.statusChanged("someOriginalUrl", RepositoryDataItemStatus.FOUND, item, context);
		assertEquals(1, urlGeneratorCount.get());
		assertEquals(1, getDataCount.get());
		assertEquals(dependantEntity, entityRef.get());
		Map<String, Object> expectedContext = IArc4EclipseRepository.Utils.makeSecondaryContext(dependantEntity, urlKey, dependantUrl);
		assertEquals(expectedContext, contextRef.get());
		assertEquals("/" + dependantEntity + "_s/276/dependanturl", urlRef.get());
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
			public IArc4EclipseRepository getRepository() {
				return new IArc4EclipseRepository() {

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
					public void addLogger(IArc4EclipseLogger logger) {
						fail();
					}

					@Override
					public void shutdown() {
						fail();
					}

					@Override
					public void notifyListenersThereIsNoData(String entity, Map<String, Object> context) {
						fail();
					}
				};
			}
		};
		return result;

	}

}
