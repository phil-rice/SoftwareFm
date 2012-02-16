/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.comments;

import java.util.Map;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.runnable.Runnables;
import org.softwareFm.common.runnable.Runnables.CountRunnable;
import org.softwareFm.swt.card.CardDataStoreFixture;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CollectionConstants;
import org.softwareFm.swt.explorer.internal.IExplorerTest;
import org.softwareFm.swt.swt.SwtTest;

public class CommentsTest extends SwtTest {

	private CountRunnable addRunnable;
	private Comments comments;
	private CardConfig cardConfig;

	public void testCommentsUseDateOrderWhenPossible() {
		Map<String, Object> data = Maps.stringObjectMap("a", 1, "b", 2, CollectionConstants.comment, Maps.stringObjectMap(//
				"key1", makeComment("title1", "text1", 3),//
				"key2", makeComment("title2", "text2", 1),//
				"key3", makeComment("title3", "text3", 2)));
		ICardData cardData = ICardData.Utils.create(cardConfig, "someCardType", "someUrl", data);
		comments.showCommentsFor(cardData);
		Table table = comments.getTable();
		checkTable(table, 0, "key1", "title1", "text1");
		checkTable(table, 1, "key3", "title3", "text3");
		checkTable(table, 2, "key2", "title2", "text2");
		assertEquals(3, table.getItemCount());
	}

	private void checkTable(Table table, int index, String key, String... strings) {
		TableItem item = table.getItem(index);
		assertEquals(table.getColumnCount(), strings.length);
		for (int i = 0; i < strings.length; i++)
			assertEquals(strings[i], item.getText(i));

	}

	private Map<String, Object> makeComment(String title, String text, int time) {
		return Maps.stringObjectMap(CollectionConstants.commentTitleKey, title, CollectionConstants.commentTextKey, text, CollectionConstants.createdTimeKey, time);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		addRunnable = Runnables.count();
		cardConfig = IExplorerTest.addNeededResources(CardDataStoreFixture.syncCardConfig(display));
		comments = new Comments(shell, cardConfig, new ICommentsCallback() {
			@Override
			public void selected(String cardType, String title, String text) {
			}
		}, addRunnable);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		cardConfig.dispose();
	}

}