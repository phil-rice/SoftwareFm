/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.explorer.internal;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

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
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class ExplorerAddingCollectionsIntegrationTest extends AbstractExplorerIntegrationTest {

	private static final String unknown = "Unknown";
	private static final String pleaseAddAName = "<Please add a name>";
	private static final String pleaseAddADescription = "<Please add a description>";

	public void testAddingCompanies() throws Exception {
		checkAdding("company", "Companies", 4, null, new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "title", "", "someTitle");
				adding.tableItem(1, "url", unknown, "someUrl");
				adding.tableItem(2, "email", "", "someEmail");
				adding.tableItem(3, "text", "", "someText");
			}
		});

	}

	public void testAddingTutorials() throws Exception {
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

	public void testAddingDocumentation() throws Exception {
		checkAdding("documentation", 3, null, new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "title", "", "some Title");
				adding.tableItem(1, "url", unknown, "someEmail");
				adding.tableItem(2, "description", pleaseAddADescription, "someDescription");
			}
		});
	}

	public void testAddingMailingList() throws Exception {
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

	public void testAddingTweets() throws Exception {
		checkAdding("tweet", 1, "tweet", new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "tweet", "", "someTweet");
			}
		});
	}

	public void testAddingRss() throws Exception {
		checkAdding("rss", 2, null, new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "url", unknown, "someUrl");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
			}
		});
	}

	public void testAddingBlog() throws Exception {
		checkAdding("article", 2, null, new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "url", unknown, "someUrl");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
			}
		});
	}

	public void testAddingForum() throws Exception {
		checkAdding("forum", 2, null, new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "url", unknown, "someUrl");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
			}
		});
	}

	public void testAddingAdvert() throws Exception {
		
		IAddingCallback<ICard> addingCallback = new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "title", "", "someTitle");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
				adding.tableItem(2, "url", unknown, "someUrl");
			}
		};
		String collectionUrl = AbstractExplorerIntegrationTest.artifactUrl + "/" + "advert";
		delete(collectionUrl);

		checkAddingToUrl(AbstractExplorerIntegrationTest.artifactUrl, new ICallback<ICard>() {
			@Override
			public void process(ICard t) throws Exception {
				Menu menu = selectAndCreatePopupMenu(t, "Adverts");
				executeMenuItem(menu, "Add " + "advert");
			}
		}, "advert", "Adverts", 3, null, addingCallback, new IFunction1<String, String>() {
			@Override
			public String apply(String from) throws Exception {
				return from + "/" + "advert";
			}
		});
		delete(collectionUrl);

		checkAddingToUrl(collectionUrl, new ICallback<ICard>() {

			@Override
			public void process(ICard t) throws Exception {
				final Menu menu1 = new Menu(shell);
				cardConfig.popupMenuService.contributeTo("popupmenuid", new Event(), menu1, t);
				Menu menu = menu1;
				executeMenuItem(menu, "Add " + "advert");
			}
		}, "advert", "Adverts", 3, null, addingCallback, new IFunction1<String, String>() {
			@Override
			public String apply(String from) throws Exception {
				return from;
			}
		});

	}

	public void testAddingRecruitment() throws Exception {
		checkAdding("job", 3, null, new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "title", "", "someTitle");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
				adding.tableItem(2, "url", unknown, "someUrl");
			}
		});
	}

	/**
	 * If urlFragment is null, expecting a UUID
	 * 
	 * @throws Exception
	 */
	private void checkAdding(final String collection, final int count, final String urlFragment, final IAddingCallback<ICard> addingCallback) throws Exception {
		final String nameInMainCard = Strings.camelCaseToPretty(collection);
		checkAdding(collection, nameInMainCard, count, urlFragment, addingCallback);

	}

	private void checkAdding(final String collection, final String nameInMainCard, final int count, final String urlFragment, final IAddingCallback<ICard> addingCallback) throws InterruptedException, ExecutionException {
		String collectionUrl = AbstractExplorerIntegrationTest.artifactUrl + "/" + collection;

		delete(collectionUrl);
		addFromRoot(collection, nameInMainCard, count, urlFragment, addingCallback);

		delete(collectionUrl);
		addFromCollection(collection, nameInMainCard, count, urlFragment, addingCallback, collectionUrl);

		makeCollectionUrlAnExistingUrl(collectionUrl);
		addFromRoot(collection, nameInMainCard, count, urlFragment, addingCallback);

		makeCollectionUrlAnExistingUrl(collectionUrl);
		addFromCollection(collection, nameInMainCard, count, urlFragment, addingCallback, collectionUrl);
	}

	private void makeCollectionUrlAnExistingUrl(String collectionUrl) throws InterruptedException, ExecutionException {
		delete(collectionUrl);
		repository.post(rootUrl + collectionUrl, Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection), IResponseCallback.Utils.noCallback()).get();
	}

	private void addFromCollection(final String collection, final String nameInMainCard, final int count, final String urlFragment, final IAddingCallback<ICard> addingCallback, String collectionUrl) {
		checkAddingToUrl(collectionUrl, new ICallback<ICard>() {

			@Override
			public void process(ICard t) throws Exception {
				final Menu menu1 = new Menu(shell);
				cardConfig.popupMenuService.contributeTo("popupmenuid", new Event(), menu1, t);
				Menu menu = menu1;
				executeMenuItem(menu, "Add " + collection);
			}
		}, collection, nameInMainCard, count, urlFragment, addingCallback, new IFunction1<String, String>() {
			@Override
			public String apply(String from) throws Exception {
				return from;
			}
		});
	}

	private void addFromRoot(final String collection, final String nameInMainCard, final int count, final String urlFragment, final IAddingCallback<ICard> addingCallback) {
		checkAddingToUrl(AbstractExplorerIntegrationTest.artifactUrl, new ICallback<ICard>() {
			@Override
			public void process(ICard t) throws Exception {
				Menu menu = selectAndCreatePopupMenu(t, nameInMainCard);
				executeMenuItem(menu, "Add " + collection);
			}
		}, collection, nameInMainCard, count, urlFragment, addingCallback, new IFunction1<String, String>() {
			@Override
			public String apply(String from) throws Exception {
				return from + "/" + collection;
			}
		});
	}

	private void delete(String collectionUrl) throws InterruptedException, ExecutionException {
		String fullUrl = rootUrl + collectionUrl;
		httpClient.delete(fullUrl).execute(IResponseCallback.Utils.noCallback()).get();
	}

	private void checkAddingToUrl(String initialUrl, final ICallback<ICard> addMenuExecutor, final String collection, final String nameInMainCard, final int count, final String urlFragment, final IAddingCallback<ICard> addingCallback, final IFunction1<String, String> baseToExpectedCollectionUrlFn) {
		displayCard(initialUrl, new CardHolderAndCardCallback() {
			@Override
			public void process(ICardHolder cardHolder, final ICard initialCard) throws Exception {
				addMenuExecutor.process(initialCard);

				Composite detailContent1 = (Composite) masterDetailSocial.getDetailContent();
				final Table cardTable = Lists.getOnly(Swts.findChildrenWithClass(detailContent1, Table.class));
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
				Composite detailContent = detailContent1;

				List<Button> buttons = Swts.findChildrenWithClass(detailContent, Button.class);
				final Button okButton = Swts.findButtonWithText(buttons, "Ok");
				final CountDownLatch latch = new CountDownLatch(1);
				ExplorerAdapter listemer = new ExplorerAdapter() {

					@SuppressWarnings("unchecked")
					@Override
					public void collectionItemAdded(String collectionUrl, String key) {
						explorer.removeExplorerListener(this);
						ICard card = explorer.cardHolder.getCard();
						assertEquals(CardConstants.collection, card.cardType());
						String expectedCollectionUrl = Functions.call(baseToExpectedCollectionUrlFn, initialCard.url());
						assertEquals(expectedCollectionUrl, collectionUrl);
						assertEquals(collectionUrl, card.url());

						if (urlFragment == null)
							UUID.fromString(key);
						else {
							Map<String, Object> data = card.data();
							Map<String, Object> dataAboutItemAdded = (Map<String, Object>) data.get(key);
							String raw = (String) dataAboutItemAdded.get(urlFragment);
							assertEquals(Strings.forUrl(raw), key);
						}

						int index = card.getTable().getSelectionIndex();
						TableItem item = card.getTable().getItem(index);
						assertEquals(key, item.getData());
						latch.countDown();
					}
				};
				explorer.addExplorerListener(listemer);
				okButton.notifyListeners(SWT.Selection, new Event());
				dispatchUntilTimeoutOrLatch(latch, delay);
			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ICardMenuItemHandler.Utils.addExplorerMenuItemHandlers(explorer, "popupmenuid");
		postArtifactData();	}

}