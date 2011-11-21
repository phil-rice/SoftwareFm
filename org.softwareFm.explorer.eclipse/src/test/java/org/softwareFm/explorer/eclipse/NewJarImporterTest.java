package org.softwareFm.explorer.eclipse;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.junit.Test;
import org.softwareFm.card.api.BasicCardConfigurator;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.CardDataStoreMock;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.IAfterEditCallback;
import org.softwareFm.configuration.ConfigurationConstants;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.callbacks.MemoryCallback;
import org.softwareFm.utilities.future.Futures;
import org.softwareFm.utilities.maps.Maps;

public class NewJarImporterTest extends TestCase {

	private CardConfig cardConfig;
	private final Map<String, Map<String, Object>> actualUrlToMap = Maps.newMap(LinkedHashMap.class);
	private final AtomicInteger count = new AtomicInteger();

	@Test
	public void test() {
		NewJarImporter newJarImporter = new NewJarImporter(cardConfig, "found - text", "012345", "g", "a", "v");
		MemoryCallback<String> memory = ICallback.Utils.<String>memory();
		newJarImporter.process(memory);
		assertEquals(Maps.makeLinkedMap(//
				"/softwareFm/jars/01/23/012345", //
				Maps.makeLinkedMap(CardConstants.slingResourceType, CardConstants.collection, ConfigurationConstants.groupId, "g", ConfigurationConstants.artifactId, "a", CardConstants.version, "v"),//
				"/prefix/g/g", //
				Maps.makeLinkedMap(CardConstants.slingResourceType, CardConstants.group, ConfigurationConstants.groupId, "g"),//
				"/prefix/g/g/artifact", //
				Maps.makeLinkedMap(CardConstants.slingResourceType, CardConstants.collection),//
				"/prefix/g/g/artifact/a", //
				Maps.makeLinkedMap(CardConstants.slingResourceType, CardConstants.artifact),//
				"/prefix/g/g/artifact/a/version", //
				Maps.makeLinkedMap(CardConstants.slingResourceType, CardConstants.collection),//
				"/prefix/g/g/artifact/a/version/v", //
				Maps.makeLinkedMap(CardConstants.slingResourceType, CardConstants.version, ConfigurationConstants.groupId, "g", ConfigurationConstants.artifactId, "a", ConfigurationConstants.version, "v"),//
				"/prefix/g/g/artifact/a/version/v/digest", //
				Maps.makeLinkedMap(CardConstants.slingResourceType, CardConstants.collection),//
				"/prefix/g/g/artifact/a/version/v/digest/012345", //
				Maps.makeLinkedMap(CardConstants.slingResourceType, CardConstants.versionJar, CardConstants.digest, "012345", CardConstants.found, "found - text")),//
				actualUrlToMap);
		assertEquals("/prefix/g/g/artifact/a", memory.getOnlyResult());
		assertEquals(actualUrlToMap.size(), count.get());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardConfig = new CardConfig(ICardFactory.Utils.cardFactory(), //
				new CardDataStoreMock(CardDataStoreFixture.dataForMocks) {
					@Override
					public Future<?> put(String url, Map<String, Object> map, IAfterEditCallback callback) {
						count.incrementAndGet();
						actualUrlToMap.put(url, map);
						callback.afterEdit(url);
						return Futures.doneFuture(null);
					}
				}).withUrlGeneratorMap(BasicCardConfigurator.makeUrlGeneratorMap("/prefix/"));
	}

}
