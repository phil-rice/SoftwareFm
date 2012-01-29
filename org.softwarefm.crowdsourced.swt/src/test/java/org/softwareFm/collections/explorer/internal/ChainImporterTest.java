package org.softwareFm.collections.explorer.internal;

import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.card.dataStore.CardDataStoreMock;
import org.softwareFm.collections.explorer.internal.NewJarImporter.ImportStageCommand;
import org.softwareFm.utilities.collections.Sets;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.runnable.Runnables;
import org.softwareFm.utilities.runnable.Runnables.CountRunnable;

public class ChainImporterTest extends TestCase {

	private ChainImporter chainImporter;
	private CardDataStoreMock cardDataStore;

	public void testPutData() {
		CountRunnable count = Runnables.count();
		Map<String, Object> data = Maps.stringObjectMap("a", 1);
		chainImporter.process(count, new NewJarImporter.ImportStage("someUrl", data, ImportStageCommand.POST_DATA));
		assertEquals(1, count.getCount());
		assertEquals(Maps.makeMap("someUrl", data), cardDataStore.updateMap);
	}

	public void testMakeRepo() {
		CountRunnable count = Runnables.count();
		chainImporter.process(count, new NewJarImporter.ImportStage("someUrl", null, ImportStageCommand.MAKE_REPO));
		assertEquals(1, count.getCount());
		assertEquals(Sets.makeSet("someUrl"), cardDataStore.repos);
	}

	public void testAFew() {
		CountRunnable count = Runnables.count();
		Map<String, Object> data1 = Maps.stringObjectMap("a", 1);
		Map<String, Object> data2 = Maps.stringObjectMap("a", 2);
		chainImporter.process(count, //
				new NewJarImporter.ImportStage("someUrl1", data1, ImportStageCommand.POST_DATA),//
				new NewJarImporter.ImportStage("someUrlr", null, ImportStageCommand.MAKE_REPO),//
				new NewJarImporter.ImportStage("someUrl2", data2, ImportStageCommand.POST_DATA));
		assertEquals(1, count.getCount());
		assertEquals(Sets.makeSet("someUrlr"), cardDataStore.repos);
		assertEquals(Maps.makeMap("someUrl1", data1, "someUrl2", data2), cardDataStore.updateMap);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cardDataStore = new CardDataStoreMock();
		chainImporter = new ChainImporter(cardDataStore);
	}
}
