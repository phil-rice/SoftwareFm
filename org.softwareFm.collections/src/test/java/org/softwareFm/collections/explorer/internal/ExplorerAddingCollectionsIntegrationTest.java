package org.softwareFm.collections.explorer.internal;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

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
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.collections.explorer.ExplorerAdapter;
import org.softwareFm.collections.menu.ICardMenuItemHandler;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.strings.Strings;

public class ExplorerAddingCollectionsIntegrationTest extends AbstractExplorerIntegrationTest {

	private static final String unknown = "Unknown";
	private static final String pleaseAddAName = "<Please add a name>";
	private static final String pleaseAddADescription = "<Please add a description>";

	public void testAddingDocumentation() {
		checkAdding("documentation", 3, null, new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "title", "", "some Title");
				adding.tableItem(1, "url", unknown, "someEmail");
				adding.tableItem(2, "description", pleaseAddADescription, "someDescription");
			}
		});
	}

	public void testAddingMailingList() {
		checkAdding("mailingList", 6, "name", new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "name", pleaseAddAName, "some Name");
				adding.tableItem(1, "email", "<Add email>", "someEmail");
				adding.tableItem(2, "description", pleaseAddADescription, "someDescription");
				adding.tableItem(3, "subscribe", "<Add email address used to subscribe>", "someSubscribe");
				adding.tableItem(4, "unsubscribe", "<Add email address used to unsubscribe> ", "someUnsubscribe");
				adding.tableItem(5, "url", "", "someUrl");
			}
		});
	}

	public void testAddingTutorials() {
		checkAdding("tutorial", "Tutorials", 4, null, new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "title", "", "someTitle");
				adding.tableItem(1, "url", unknown, "someUrl");
				adding.tableItem(2, "description", pleaseAddADescription, "someDescription");
				adding.tableItem(3, "author", "", "someAuthor");
			}
		});
	}

	public void testAddingTweets() {
		checkAdding("tweet", 1, "tweet", new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "tweet", "", "someTweet");
			}
		});
	}

	public void testAddingRss() {
		checkAdding("rss", 2, null, new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "url", unknown, "someUrl");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
			}
		});
	}

	public void testAddingBlog() {
		checkAdding("blog", 2, null, new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "url", unknown, "someUrl");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
			}
		});
	}

	public void testAddingForum() {
		checkAdding("forum", 2, null, new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "url", unknown, "someUrl");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
			}
		});
	}

	public void testAddingAdvert() {
		checkAdding("advert", "Adverts", 3, null, new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "title", "", "someTitle");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
				adding.tableItem(2, "url", unknown, "someUrl");
			}
		});

	}

	public void testAddingRecruitment() {
		checkAdding("recruitment", 3, null, new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "title", "", "someTitle");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
				adding.tableItem(2, "url", unknown, "someUrl");
			}
		});

	}

	/** If urlFragment is null, expecting a UUID */
	private void checkAdding(final String collection, final int count, final String urlFragment, final IAddingCallback<ICard> addingCallback) {
		checkAdding(collection, Strings.camelCaseToPretty(collection), count, urlFragment, addingCallback);

	}

	private void checkAdding(final String collection, final String nameInMainCard, final int count, final String urlFragment, final IAddingCallback<ICard> addingCallback) {
		displayCard(AbstractExplorerIntegrationTest.artifactUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, final ICard initialCard) throws Exception {
				Menu menu = selectAndCreatePopupMenu(initialCard, nameInMainCard);
				executeMenuItem(menu, "Add " + collection);

				Composite detailContent = (Composite) masterDetailSocial.getDetailContent();
				final Table cardTable = Lists.getOnly(Swts.findChildrenWithClass(detailContent, Table.class));
				assertEquals(count, cardTable.getItemCount());
				addingCallback.process(false, initialCard, new IAdding() {
					@Override
					public void tableItem(int index, String name, String existing, String newValue) {
						String prettyName = Strings.camelCaseToPretty(name);
						TableItem item = cardTable.getItem(index);
						assertEquals(prettyName, item.getText(0));
						assertEquals(existing, item.getText(1));
						cardTable.select(index);
						cardTable.notifyListeners(SWT.Selection, new Event());
						Text text = Swts.findChildrenWithClass(cardTable, Text.class).get(index);
						assertEquals(existing, text.getText());
						text.setText(newValue);
					}
				});

				List<Button> buttons = Swts.findChildrenWithClass(detailContent, Button.class);
				final Button okButton = Swts.findButtonWithText(buttons, "Ok");
				final CountDownLatch latch = new CountDownLatch(1);
				explorer.addExplorerListener(new ExplorerAdapter() {

					@Override
					public void collectionItemAdded(String collectionUrl, String key) {
						explorer.removeExplorerListener(this);
						latch.countDown();
						ICard card = explorer.cardHolder.getCard();
						assertEquals(CardConstants.collection, card.cardType());
						assertEquals(initialCard.url() + "/" + collection, card.url());
						assertEquals(collectionUrl, card.url());

						if (urlFragment == null)
							UUID.fromString(key);
						else {
							Map<String, Object> data = card.data();
							Map<String,Object> dataAboutItemAdded = (Map<String, Object>) data.get(key);
							String raw = (String) dataAboutItemAdded.get(urlFragment);
							assertEquals(Strings.forUrl(raw), key);
						}

						int index = card.getTable().getSelectionIndex();
						TableItem item = card.getTable().getItem(index);
						assertEquals(key, item.getData());

						// not checking that the notify listener has being called...either showing browser or card
					}
				});
				okButton.notifyListeners(SWT.Selection, new Event());
				dispatchUntilTimeoutOrLatch(latch, delay);
			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ICardMenuItemHandler.Utils.addExplorerMenuItemHandlers(explorer, "popupmenuid");
	}

}
