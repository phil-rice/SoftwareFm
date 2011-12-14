package org.softwareFm.collections.comments;

import java.util.Map;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.collections.explorer.internal.IExplorerTest;
import org.softwareFm.display.swt.SwtTest;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.runnable.Runnables;
import org.softwareFm.utilities.runnable.Runnables.CountRunnable;

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
		return Maps.stringObjectMap(CollectionConstants.commentTitleKey, title, CollectionConstants.commentTextKey, text, CollectionConstants.createdTime, time);
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

}
