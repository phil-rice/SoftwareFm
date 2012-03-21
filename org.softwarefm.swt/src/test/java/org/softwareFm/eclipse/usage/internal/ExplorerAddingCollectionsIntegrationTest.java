/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.usage.internal;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.editors.IDataCompositeWithOkCancel;
import org.softwareFm.swt.explorer.ExplorerAdapter;
import org.softwareFm.swt.menu.ICardMenuItemHandler;
import org.softwareFm.swt.swt.Swts;

public abstract class ExplorerAddingCollectionsIntegrationTest extends AbstractExplorerIntegrationTest {
	abstract protected void addMe(String collection, String collectionUrl, String nameInMainCard, int count, String urlFragment, IAddingCallback<ICard> addingCallback) throws Exception, ExecutionException;

	private static final String unknown = "Unknown";
	private static final String pleaseAddAName = "<Please add a name>";
	private static final String pleaseAddADescription = "<Please add a description>";
	private String popupMenuId;

	public void testAddingAdvert() throws Exception {

		IAddingCallback<ICard> addingCallback = new IAddingCallback<ICard>() {
			@Override
			public void process(boolean added, ICard card, IAdding adding) {
				adding.tableItem(0, "title", "", "someTitle");
				adding.tableItem(1, "description", pleaseAddADescription, "someDescription");
				adding.tableItem(2, "url", unknown, "someUrl");
			}
		};
		checkAdding("advert", "Adverts", 3, null, addingCallback);
	}

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

	private void checkAdding(final String collection, final String nameInMainCard, final int count, final String urlFragment, final IAddingCallback<ICard> addingCallback) {
		try {
			String collectionUrl = Urls.composeWithSlash(AbstractExplorerIntegrationTest.artifactUrl, collection);
			addMe(collection, collectionUrl, nameInMainCard, count, urlFragment, addingCallback);

		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	protected void makeCollectionUrlAndExistingUrl(final String collectionUrl) {
		getLocalApi().makeReadWriter().access(IGitLocal.class, new IFunction1<IGitLocal, Void>() {
			@Override
			public Void apply(IGitLocal gitLocal) throws Exception {
				gitLocal.put(IFileDescription.Utils.compose(rootArtifactUrl, collectionUrl), Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection));
				return null;
			}
		});
	}

	protected void addFromCollection(final String collection, final String nameInMainCard, final int count, final String urlFragment, final IAddingCallback<ICard> addingCallback, String collectionUrl) {
		checkAddingToUrl(collectionUrl, new ICallback<ICard>() {

			@Override
			public void process(ICard t) throws Exception {
				final Menu menu1 = new Menu(shell);
				cardConfig.popupMenuService.contributeTo(popupMenuId, new Event(), menu1, t);
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

	protected void addFromRoot(final String collection, final String nameInMainCard, final int count, final String urlFragment, final IAddingCallback<ICard> addingCallback) {
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
				@SuppressWarnings("unchecked")
				Control okButton = ((IDataCompositeWithOkCancel<Composite>) detailContent1).getFooter().okButton();
				final CountDownLatch latch = new CountDownLatch(1);
				ExplorerAdapter listemer = new ExplorerAdapter() {

					@SuppressWarnings("unchecked")
					@Override
					public void collectionItemAdded(String collectionUrl, String key) {
						explorer.removeExplorerListener(this);
						ICard card = explorer.cardHolder.getCard();
						assertEquals(card.toString(), CardConstants.collection, card.cardType());
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
				Swts.Buttons.press(okButton);
				dispatchUntilTimeoutOrLatch(latch);
			}
		});
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		popupMenuId = getClass().getSimpleName();
		ICardMenuItemHandler.Utils.addExplorerMenuItemHandlers(explorer, popupMenuId);
		postArtifactData();
	}

}