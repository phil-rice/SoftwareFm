/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.explorer.internal;

import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.utilities.collections.Sets;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.runnable.Runnables;
import org.softwareFm.swt.card.dataStore.CardDataStoreMock;
import org.softwareFm.swt.explorer.internal.NewJarImporter.ImportStageCommand;

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