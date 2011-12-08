package org.softwareFm.collections.explorer.internal;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.collections.explorer.ExplorerAdapter;
import org.softwareFm.collections.menu.ICardMenuItemHandler;
import org.softwareFm.utilities.collections.Lists;

public class ExplorerIntegrationTest extends AbstractExplorerIntegrationTest {
	public void testShowContentOnlyAsksForOneMainUrlFromCardDataStore() {
		final AtomicInteger count = new AtomicInteger();
		displayCard(AbstractExplorerIntegrationTest.artifactUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws InterruptedException {
				selectItem(card, "Tutorials");
				final CountDownLatch latch = new CountDownLatch(1);
				explorer.addExplorerListener(new ExplorerAdapter() {
					@Override
					public void initialCard(ICardHolder cardHolder, CardConfig cardConfig, String url, ICard card) {
						assertEquals(1, count.incrementAndGet());
						latch.countDown();
						assertEquals(rootUrl + AbstractExplorerIntegrationTest.artifactUrl + "/tutorial", card.url());
					}
				});
				explorer.showContents();
				dispatchUntilTimeoutOrLatch(latch, delay);

			}
		});
		assertEquals(1, count.get());
	}

	public void testViewTutorials() {
		displayCardThenViewChild(AbstractExplorerIntegrationTest.artifactUrl, "Tutorials", new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				assertEquals(rootUrl + AbstractExplorerIntegrationTest.artifactUrl + "/tutorial", card.url());
				assertEquals(explorer.getBrowser().getComposite(), masterDetailSocial.getDetailContent());
			}
		});
	}

	public void testAddMailingListCausesCardEditorToAppear() {
		displayCard(AbstractExplorerIntegrationTest.artifactUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				Menu menu = selectAndCreatePopupMenu(card, "Mailing List");
				executeMenuItem(menu, "Add mailingList");

				Composite detailContent = (Composite) masterDetailSocial.getDetailContent();
				assertEquals("CardEditorComposite", detailContent.getClass().getSimpleName());
				assertEquals(2, detailContent.getChildren().length);// title,body
			}
		});
	}

	public void testArtifactViewMainTitles() {
		displayCard(AbstractExplorerIntegrationTest.artifactUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) {
				Table table = card.getTable();
				assertEquals(10, table.getItemCount());
				assertEquals("Name", table.getItem(0).getText(0));
				assertEquals("Description", table.getItem(1).getText(0));
				assertEquals("Issues", table.getItem(2).getText(0));
				assertEquals("Version", table.getItem(3).getText(0));
				assertEquals("Mailing List", table.getItem(4).getText(0));
				assertEquals("Tutorials", table.getItem(5).getText(0));
				assertEquals("Tweet", table.getItem(6).getText(0));
				assertEquals("Rss", table.getItem(7).getText(0));
				assertEquals("Blog", table.getItem(8).getText(0));
				assertEquals("Forum", table.getItem(9).getText(0));
			}
		});
	}

	public void testRightClickMenusText() {
		String view = "View";
		String edit = "Edit";

		checkMenu(0, "Name", edit, view);
		checkMenu(1, "Description", edit, view);
		checkMenu(2, "Issues", edit, view);
		checkMenu(3, "Version", view, "Add version");
		checkMenu(4, "Mailing List", view, "Add mailingList");
		checkMenu(5, "Tutorials", view, "Add tutorial");
		checkMenu(6, "Tweet", view, "Add tweet");
		checkMenu(7, "Rss", view, "Add rss");
		checkMenu(8, "Blog", view, "Add blog");
	}

	private void checkMenu(final int index, final String expectedName, final String... expected) {
		displayCard(AbstractExplorerIntegrationTest.artifactUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) {
				final Menu menu = new Menu(shell);
				Table table = card.getTable();
				table.select(index);
				cardConfig.popupMenuService.contributeTo("popupmenuid", new Event(), menu, card);
				List<String> actual = Lists.newList();
				for (int i = 0; i < menu.getItemCount(); i++)
					actual.add(menu.getItem(i).getText());
				assertEquals(expectedName, table.getItem(index).getText(0));
				List<String> fullExpected = Lists.append(Arrays.asList(expected), "", "Register new artifact");
				assertEquals(fullExpected, actual);
			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ICardMenuItemHandler.Utils.addExplorerMenuItemHandlers(explorer, "popupmenuid");
	}

}
