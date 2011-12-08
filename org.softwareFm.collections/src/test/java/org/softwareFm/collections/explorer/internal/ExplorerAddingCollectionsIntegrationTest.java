package org.softwareFm.collections.explorer.internal;

import java.util.List;
import java.util.UUID;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.collections.menu.ICardMenuItemHandler;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.strings.Strings;

public class ExplorerAddingCollectionsIntegrationTest extends AbstractExplorerIntegrationTest {

	private static final String unknown = "Unknown";
	private static final String pleaseAddAName = "<Please add a name>";
	private static final String pleaseAddADescription = "<Please add a description>";

	static interface IAddingCallback {
		/** will get called twice. If added is false, card is the initial card, if false card is the added card */
		void process(boolean added, ICard card, IAdding adding);
	}

	static interface IAdding {
		void tableItem(int index, String name, String existing, String newValue);
	}

	public void testAddingMailingList() {
		checkAdding("mailingList", 6, "name", new IAddingCallback() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "name", pleaseAddAName, "some Name");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
				adding.tableItem(2, "email", "<Add email>", "someEmail");
				adding.tableItem(3, "subscribe", "<Add email address used to subscribe>", "someSubscribe");
				adding.tableItem(4, "unsubscribe", "<Add email address used to unsubscribe> ", "someUnsubscribe");
				adding.tableItem(5, "url", "", "someUrl");
			}
		});
	}

	public void testAddingTutorials() {
		checkAdding("tutorial", "Tutorials", 3, null, new IAddingCallback() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "title", "", "someTitle");
				adding.tableItem(1, "url", unknown, "someUrl");
				adding.tableItem(2, "description", pleaseAddADescription, "someDescription");
			}
		});
	}

	public void testAddingTweets() {
		checkAdding("tweet", 1, "tweet", new IAddingCallback() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "tweet", "", "someTweet");
			}
		});
	}

	public void testAddingRss() {
		checkAdding("rss", 2, null, new IAddingCallback() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "url", unknown, "someUrl");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
			}
		});
	}

	public void testAddingBlog() {
		checkAdding("blog", 2, null, new IAddingCallback() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "url", unknown, "someUrl");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
			}
		});
	}

	public void testAddingForum() {
		checkAdding("forum", 2, null, new IAddingCallback() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "url", unknown, "someUrl");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
			}
		});
	}

	public void testAddingAdvert() {
		checkAdding("advert", "Adverts", 3, null, new IAddingCallback() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "title", "", "someTitle");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
				adding.tableItem(2, "url", unknown, "someUrl");
			}
		});

	}

	public void testAddingRecruitment() {
		checkAdding("recruitment", 3, null, new IAddingCallback() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "title", "", "someTitle");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
				adding.tableItem(2, "url", unknown, "someUrl");
			}
		});

	}

	/** If urlFragment is null, expecting a UUID */
	private void checkAdding(final String collection, final int count, final String urlFragment, final IAddingCallback addingCallback) {
		checkAdding(collection, Strings.camelCaseToPretty(collection), count, urlFragment, addingCallback);

	}

	private void checkAdding(final String collection, final String nameInMainCard, final int count, final String urlFragment, final IAddingCallback addingCallback) {
		displayCard(AbstractExplorerIntegrationTest.artifactUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, ICard card) throws Exception {
				Menu menu = selectAndCreatePopupMenu(card, nameInMainCard);
				executeMenuItem(menu, "Add " + collection);

				Composite detailContent = (Composite) masterDetailSocial.getDetailContent();
				final Table cardTable = Lists.getOnly(Swts.findChildrenWithClass(detailContent, Table.class));
				assertEquals(count, cardTable.getItemCount());
				addingCallback.process(false, card, new IAdding() {
					@Override
					public void tableItem(int index, String name, String existing, String newValue) {
						String prettyName = Strings.camelCaseToPretty(name);
						TableItem item = cardTable.getItem(index);
						assertEquals(prettyName, item.getText(0));
						assertEquals(existing, item.getText(1));
						cardTable.select(index);
						cardTable.notifyListeners(SWT.Selection, new Event());
						Text text = Lists.getOnly(Swts.findChildrenWithClass(cardTable, Text.class));
						assertEquals(existing, text.getText());
						text.setText(newValue);
					}
				});

				List<Button> buttons = Swts.findChildrenWithClass(detailContent, Button.class);
				final Button okButton = Swts.findButtonWithText(buttons, "Ok");
				doSomethingAndWaitForCardDataStoreToFinish(new Runnable() {
					@Override
					public void run() {
						okButton.notifyListeners(SWT.Selection, new Event());
					}
				}, new CardHolderAndCardCallback() {
					@Override
					public void process(ICardHolder cardHolder, final ICard card) throws Exception {
						assertEquals(collection, card.cardType());
						addingCallback.process(true, card, new IAdding() {
							@Override
							public void tableItem(int index, String name, String existing, String newValue) {
								assertEquals(newValue, card.data().get(name));
							}
						});
						String url = card.url();
						String lastSegment = Strings.lastSegment(url, "/");
						if (urlFragment == null)
							UUID.fromString(lastSegment);
						else
							assertEquals(Strings.forUrl((String) card.data().get(urlFragment)), lastSegment);
					}

				});

			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ICardMenuItemHandler.Utils.addExplorerMenuItemHandlers(explorer, "popupmenuid");
	}

}
