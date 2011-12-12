/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.collections.explorer.internal;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardChangedListener;
import org.softwareFm.card.card.ICardData;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.card.ILineSelectedListener;
import org.softwareFm.card.card.RightClickCategoryResult;
import org.softwareFm.card.card.composites.TextInBorder;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.card.dataStore.IAfterEditCallback;
import org.softwareFm.card.dataStore.ICardAndCollectionDataStoreVisitor;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.card.dataStore.ICardDataStoreCallback;
import org.softwareFm.card.dataStore.IMutableCardDataStore;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.card.editors.ICardEditorCallback;
import org.softwareFm.card.editors.IEditorDetailAdder;
import org.softwareFm.card.editors.IValueEditor;
import org.softwareFm.card.navigation.internal.NavNextHistoryPrevConfig;
import org.softwareFm.collections.ICollectionConfigurationFactory;
import org.softwareFm.collections.comments.Comments;
import org.softwareFm.collections.comments.ICommentsCallback;
import org.softwareFm.collections.constants.CollectionConstants;
import org.softwareFm.collections.explorer.BrowserAndNavBar;
import org.softwareFm.collections.explorer.IExplorer;
import org.softwareFm.collections.explorer.IExplorerListener;
import org.softwareFm.collections.explorer.IMasterDetailSocial;
import org.softwareFm.display.browser.IBrowserPart;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.display.timeline.PlayItem;
import org.softwareFm.display.timeline.TimeLine;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.history.IHistoryListener;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.services.IServiceExecutor;
import org.softwareFm.utilities.strings.Strings;

public class Explorer implements IExplorer {

	ICardHolder cardHolder;
	private ICallback<String> callbackToGotoUrlAndUpdateDetails;
	private final IMasterDetailSocial masterDetailSocial;
	private final CardConfig cardConfig;
	private BrowserAndNavBar browser;
	private TimeLine timeLine;
	private Comments comments;

	public Explorer(final CardConfig cardConfig, final String rootUrl, final IMasterDetailSocial masterDetailSocial, final IServiceExecutor service, IPlayListGetter playListGetter) {
		this.cardConfig = cardConfig;
		this.masterDetailSocial = masterDetailSocial;
		callbackToGotoUrlAndUpdateDetails = new ICallback<String>() {
			@Override
			public void process(String url) throws Exception {
				masterDetailSocial.setMaster(cardHolder.getControl());
				cardConfig.cardCollectionsDataStore.processDataFor(cardHolder, cardConfig, url, new CardAndCollectionDataStoreAdapter() {
					@Override
					public void finished(ICardHolder cardHolder, String url, ICard card) {
						String key = findDefaultChild(card);
						showDetailForCardKey(card, key, card.data().get(key));
					}
				});
			}
		};
		cardHolder = masterDetailSocial.createMaster(new IFunction1<Composite, ICardHolder>() {
			@Override
			public ICardHolder apply(Composite from) throws Exception {
				ICardHolder cardHolder = ICardHolder.Utils.cardHolderWithLayout(from, cardConfig, rootUrl, callbackToGotoUrlAndUpdateDetails);
				return cardHolder;
			}
		}, true);
		cardHolder.addLineSelectedListener(new ILineSelectedListener() {
			@Override
			public void selected(final ICard card, final String key, final Object value) {
				showDetailForCardKey(card, key, value);
			}

		});
		timeLine = new TimeLine(playListGetter);
		browser = masterDetailSocial.createDetail(new IFunction1<Composite, BrowserAndNavBar>() {
			@Override
			public BrowserAndNavBar apply(Composite from) throws Exception {
				BrowserAndNavBar browserAndNavBar = new BrowserAndNavBar(from, SWT.NULL, cardConfig.leftMargin, cardConfig, new NavNextHistoryPrevConfig<PlayItem>(cardConfig.titleHeight, cardConfig.imageFn, new IFunction1<PlayItem, String>() {
					@Override
					public String apply(PlayItem from) throws Exception {
						return from.toString();
					}
				}, new ICallback<PlayItem>() {
					@Override
					public void process(PlayItem t) throws Exception {
						browser.processUrl(t.feedType, t.url);
					}
				}), service, timeLine);
				browserAndNavBar.getComposite().setLayout(new BrowserAndNavBar.BrowserAndNavBarLayout());
				return browserAndNavBar;
			}
		}, true);
		comments = masterDetailSocial.createSocial(new IFunction1<Composite, Comments>() {
			@Override
			public Comments apply(Composite from) throws Exception {
				return new Comments(from, cardConfig, new ICommentsCallback() {
					@Override
					public void selected(String cardType, String title, String text) {
						masterDetailSocial.createAndShowDetail(TextInBorder.makeTextFromString(SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL, cardConfig, cardType, "<" + text + ">\n" + title));

					}
				});
			}
		}, true);

	}

	@Override
	public void edit(final ICard card, final String key) {
		if (card.getControl().isDisposed())
			return;
		String editorName = IResourceGetter.Utils.getOr(card.getCardConfig().resourceGetterFn, card.cardType(), "editor." + key, "text");
		final IEditorDetailAdder editor = Functions.call(card.getCardConfig().editorFn, editorName);
		masterDetailSocial.showSocial();
		masterDetailSocial.createAndShowDetail(new IFunction1<Composite, IHasControl>() {
			@Override
			public IHasControl apply(Composite from) throws Exception {
				String value = Strings.nullSafeToString(card.data().get(key));
				IHasControl hasControl = editor.add(from, card, cardConfig, key, value, makeEditCallback(card));
				return hasControl;
			}
		});

	}

	@Override
	public void showAddCollectionItemEditor(final ICard card, final RightClickCategoryResult result) {
		masterDetailSocial.createAndShowDetail(new IFunction1<Composite, IValueEditor>() {
			@Override
			public IValueEditor apply(Composite from) throws Exception {
				Map<String, Object> data = Maps.stringObjectMap();
				final String collectionName = result.collectionName;
				String titlePattern = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, collectionName, CardConstants.menuItemAddCollection);
				String title = MessageFormat.format(titlePattern, result.collectionName);
				return IValueEditor.Utils.cardEditorWithLayout(from, card.getCardConfig(), title, result.collectionName, result.url, data, new ICardEditorCallback() {
					@Override
					public void ok(ICardData cardData) {
						IMutableCardDataStore store = (IMutableCardDataStore) cardConfig.cardDataStore;
						Map<String, Object> newData = cardData.data();
						String cardUrl = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, collectionName, CardConstants.cardNameUrlKey);
						String cardNameKey = IResourceGetter.Utils.getOrNull(cardConfig.resourceGetterFn, collectionName, CardConstants.cardNameFieldKey);
						String cardName = (String) newData.get(cardNameKey);
						String itemUrlFragment = MessageFormat.format(cardUrl, Strings.forUrl(cardName), makeRandomUUID());

						IMutableCardDataStore.Utils.addCollectionItem(store, result, itemUrlFragment, newData, new IAfterEditCallback() {
							@Override
							public void afterEdit(String url) {
								System.out.println("Stores into: " + url);
								displayCard(url, new CardAndCollectionDataStoreAdapter());
							}
						});
					}

					@Override
					public void cancel(ICardData cardData) {
						next();
					}

					@Override
					public boolean canOk(Map<String, Object> data) {
						return true;
					}
				});
			}
		});
	}

	@Override
	public void editSnippet(final ICard card, final String key) {
		final IMutableCardDataStore store = (IMutableCardDataStore) card.getCardConfig().cardDataStore;
		masterDetailSocial.showSocial();
		masterDetailSocial.createAndShowDetail(new IFunction1<Composite, IValueEditor>() {
			@Override
			public IValueEditor apply(Composite from) throws Exception {
				final String url = card.url() + "/" + key;
				@SuppressWarnings("unchecked")
				Map<String, Object> data = (Map<String, Object>) card.data().get(key);
				return IValueEditor.Utils.cardEditorWithLayout(from, cardConfig, "Snippet", CardConstants.snippet, url, data, new ICardEditorCallback() {
					@Override
					public void ok(final ICardData cardData) {
						Map<String, Object> data = cardData.data();
						IAfterEditCallback callback = new IAfterEditCallback() {
							@Override
							public void afterEdit(String url) {
								displayCard(card.url(), new CardAndCollectionDataStoreAdapter() {
									@Override
									public void finished(ICardHolder cardHolder, String url, ICard card) throws Exception {
										Table table = card.getTable();
										for (int i = 0; i < table.getItemCount(); i++) {
											TableItem item = table.getItem(i);
											String itemKey = (String) item.getData();
											if (key.equals(itemKey))
												table.select(i);

										}
									}
								});
								browser.processUrl(DisplayConstants.snippetFeedType, url);
								masterDetailSocial.setDetail(browser.getControl());
							}
						};
						store.put(url, data, callback);
					}

					@Override
					public void cancel(ICardData cardData) {
					}

					@Override
					public boolean canOk(Map<String, Object> data) {
						return true;
					}
				});
			}
		});
	}

	@Override
	public void showAddSnippetEditor(final ICard card) {
		final IMutableCardDataStore store = (IMutableCardDataStore) card.getCardConfig().cardDataStore;
		final String lastSegment = Strings.lastSegment(card.url(), "/");
		masterDetailSocial.showSocial();
		masterDetailSocial.createAndShowDetail(new IFunction1<Composite, IValueEditor>() {
			@Override
			public IValueEditor apply(Composite from) throws Exception {
				return IValueEditor.Utils.cardEditorWithLayout(from, cardConfig, "Snippet", CardConstants.snippet, card.url(), Maps.stringObjectMap(), new ICardEditorCallback() {
					@Override
					public void ok(final ICardData cardData) {
						final String fragment = UUID.randomUUID().toString();
						String url = cardData.url();
						Map<String, Object> data = cardData.data();
						IAfterEditCallback callback = new IAfterEditCallback() {
							@Override
							public void afterEdit(String url) {
								displayCard(card.url(), new CardAndCollectionDataStoreAdapter() {
									@Override
									public void finished(ICardHolder cardHolder, String url, ICard card) throws Exception {
										Table table = card.getTable();
										for (int i = 0; i < table.getItemCount(); i++) {
											TableItem item = table.getItem(i);
											String key = (String) item.getData();
											if (fragment.equals(key))
												table.select(i);

										}
									}
								});
								browser.processUrl(DisplayConstants.snippetFeedType, url);
								masterDetailSocial.setDetail(browser.getControl());
							}
						};
						if (lastSegment.equals(CardConstants.snippet)) {
							IMutableCardDataStore.Utils.addCollectionItemToCollection(store, url, CardConstants.snippet, fragment, data, callback);
						} else
							IMutableCardDataStore.Utils.addCollectionItemToBase(store, url, CardConstants.snippet, fragment, data, callback);
					}

					@Override
					public void cancel(ICardData cardData) {
					}

					@Override
					public boolean canOk(Map<String, Object> data) {
						return true;
					}
				});
			}
		});

	}

	@Override
	public IBrowserPart register(String feedType, IFunction1<Composite, IBrowserPart> feedPostProcessor) {
		return browser.register(feedType, feedPostProcessor);
	}

	@Override
	public void displayCard(final String url, final ICardAndCollectionDataStoreVisitor visitor) {
		fireListeners(new ICallback<IExplorerListener>() {
			@Override
			public void process(IExplorerListener t) throws Exception {
				t.displayCard(url);

			}
		});
		masterDetailSocial.showMaster();
		masterDetailSocial.setMaster(cardHolder.getControl());
		cardConfig.cardCollectionsDataStore.processDataFor(cardHolder, cardConfig, url, new ICardAndCollectionDataStoreVisitor() {
			@Override
			public void requestingFollowup(final ICardHolder cardHolder, final String url, final ICard card, final String followOnUrlFragment) {
				fireListeners(new ICallback<IExplorerListener>() {
					@Override
					public void process(IExplorerListener t) throws Exception {
						t.requestingFollowup(cardHolder, url, card, followOnUrlFragment);
					}
				});
				visitor.requestingFollowup(cardHolder, url, card, followOnUrlFragment);
			}

			@Override
			public void noData(final ICardHolder cardHolder, final String url, final ICard card, final String followUpUrl) {
				fireListeners(new ICallback<IExplorerListener>() {
					@Override
					public void process(IExplorerListener t) throws Exception {
						t.noData(cardHolder, url, card, followUpUrl);
					}
				});
				visitor.noData(cardHolder, url, card, followUpUrl);
			}

			@Override
			public void initialUrl(final ICardHolder cardHolder, final CardConfig cardConfig, final String url) {
				fireListeners(new ICallback<IExplorerListener>() {
					@Override
					public void process(IExplorerListener t) throws Exception {
						t.initialUrl(cardHolder, cardConfig, url);
					}
				});
				visitor.initialUrl(cardHolder, cardConfig, url);
			}

			@Override
			public void initialCard(final ICardHolder cardHolder, final CardConfig cardConfig, final String url, final ICard card) {
				fireListeners(new ICallback<IExplorerListener>() {
					@Override
					public void process(IExplorerListener t) throws Exception {
						t.initialCard(cardHolder, cardConfig, url, card);
					}
				});
				visitor.initialCard(cardHolder, cardConfig, url, card);
			}

			@Override
			public void followedUp(final ICardHolder cardHolder, final String url, final ICard card, final String followUpUrl, final Map<String, Object> result) {
				fireListeners(new ICallback<IExplorerListener>() {
					@Override
					public void process(IExplorerListener t) throws Exception {
						t.followedUp(cardHolder, url, card, followUpUrl, result);
					}
				});
				visitor.followedUp(cardHolder, url, card, followUpUrl, result);
			}

			@Override
			public void finished(final ICardHolder cardHolder, final String url, final ICard card) throws Exception {
				fireListeners(new ICallback<IExplorerListener>() {
					@Override
					public void process(IExplorerListener t) throws Exception {
						t.finished(cardHolder, url, card);
					}
				});
				visitor.finished(cardHolder, url, card);
			}
		});
	}

	@Override
	public void displayUnrecognisedJar(final File file, final String digest, final String projectName) {
		masterDetailSocial.showMaster();
		masterDetailSocial.createAndShowMaster(TextInBorder.makeTextWithClick(SWT.WRAP | SWT.READ_ONLY, cardConfig, new Runnable() {
			@Override
			public void run() {
				final Map<String, Object> startData = Maps.stringObjectMap(//
						CollectionConstants.groupId, "Please specify the group id",//
						CollectionConstants.artifactId, Strings.withoutVersion(file, "Please specify the artifact id"),//
						CollectionConstants.version, Strings.versionPartOf(file, "Please specify the version"));
				addUnrecognisedJar(file, digest, projectName, startData);
			}
		}, CollectionConstants.unrecognisedJarCardType, CollectionConstants.jarNotRecognisedText, file, file.getName(), projectName));
	}

	private void addUnrecognisedJar(final File file, final String digest, String projectName, final Map<String, Object> startData) {
		masterDetailSocial.showSocial();
		IFunction1<Composite, TextInBorder> text = TextInBorder.makeText(SWT.WRAP | SWT.READ_ONLY, cardConfig,CollectionConstants.unrecognisedJarCardType, CollectionConstants.unrecognisedJarThankYou, file, file.getName(), projectName);
		TextInBorder hasText = masterDetailSocial.createAndShowMaster(text);
		hasText.setMenu(new IFunction1<Control, Menu>() {
			@Override
			public Menu apply(Control from) throws Exception {
				Menu menu = new Menu(from);
				IResourceGetter resourceGetter = cardConfig.resourceGetterFn.apply(null);
				final String fileName = file.getName();
				Swts.addMenu(menu, resourceGetter, CardConstants.menuItemBrowseJarKey, new Runnable() {
					@Override
					public void run() {
						try {
							Desktop.getDesktop().browse(new URI("http://www.google.co.uk/?hl=en&q=" + Strings.forUrl(fileName)));
						} catch (Exception e) {
							throw WrappedException.wrap(e);
						}
					}
				});
				Swts.addMenu(menu, resourceGetter, CardConstants.menuItemBrowseJarMavenKey, new Runnable() {
					@Override
					public void run() {
						try {
							Desktop.getDesktop().browse(new URI("http://www.google.co.uk/?hl=en&q=" + Strings.forUrl(fileName) + "+Maven"));
						} catch (Exception e) {
							throw WrappedException.wrap(e);
						}
					}
				});
				return menu;
			}
		});
		masterDetailSocial.createAndShowDetail(new IFunction1<Composite, IValueEditor>() {
			@Override
			public IValueEditor apply(Composite from) throws Exception {
				String jarTitle = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, null, CollectionConstants.jarNotRecognisedTitle);
				return IValueEditor.Utils.cardEditorWithLayout(from, cardConfig, jarTitle, CollectionConstants.unrecognisedJarCardType, "", startData, new ICardEditorCallback() {
					@Override
					public void ok(ICardData cardData) {
						String groupId = (String) cardData.data().get(CollectionConstants.groupId);
						String artifactId = (String) cardData.data().get(CollectionConstants.artifactId);
						String version = (String) cardData.data().get(CollectionConstants.version);
						new NewJarImporter(cardConfig, CardConstants.manuallyAdded, digest, groupId, artifactId, version).process(new ICallback<String>() {
							@Override
							public void process(String url) throws Exception {
								displayCard(url, new CardAndCollectionDataStoreAdapter());
							}
						});
					}

					@Override
					public void cancel(ICardData cardData) {
					}

					@Override
					public boolean canOk(Map<String, Object> data) {
						return false;
					}
				});
			}
		});
	}

	@Override
	public void displayHelpText(final String url) {
		masterDetailSocial.createAndShowSocial(Swts.styledTextFn(url, SWT.WRAP));
		masterDetailSocial.showSocial();
	}

	private String findDefaultChild(ICard card) {
		String result = Functions.call(card.getCardConfig().defaultChildFn, card);
		return result;
	}

	@SuppressWarnings("unchecked")
	private void browseDetailForCardKey(ICard card, String key, Object value) {
		masterDetailSocial.hideSocial();
		masterDetailSocial.setDetail(browser.getControl());
		if (value instanceof Map<?, ?>) {
			Map<String, Object> map = (Map<String, Object>) value;
			String cardType = (String) map.get(CardConstants.slingResourceType);
			if (cardType != null) {
				String cardDetailExtensionKey = IResourceGetter.Utils.getOrNull(card.getCardConfig().resourceGetterFn, cardType, CardConstants.cardContentUrl);
				String cardDetailUrlKey = IResourceGetter.Utils.getOrNull(card.getCardConfig().resourceGetterFn, cardType, CardConstants.cardContentField);
				String feedType = IResourceGetter.Utils.getOrNull(card.getCardConfig().resourceGetterFn, cardType, CardConstants.cardContentFeedType);
				String url = cardDetailExtensionKey == null ? (String) map.get(cardDetailUrlKey) : MessageFormat.format(cardDetailExtensionKey, card.url(), key);
				if (url != null) {
					String cardDetailFeedType = IResourceGetter.Utils.getOr(card.getCardConfig().resourceGetterFn, cardType, CardConstants.cardContentFeedType, feedType);
					browser.processUrl(cardDetailFeedType, url);
				}
			}
		}
	}

	private void showDetailForCardKey(final ICard card, final String key, final Object value) {
		if (CardConstants.collection.equals(card.cardType())) {
			String collectionType = Strings.lastSegment(card.url(), "/");

			String hasCardContentField = IResourceGetter.Utils.getOrNull(cardConfig.resourceGetterFn, collectionType, CardConstants.cardContentField);
			if (hasCardContentField != null) {
				browseDetailForCardKey(card, key, value);
				return;
			}
		}

		masterDetailSocial.createAndShowDetail(new IFunction1<Composite, IHasControl>() {
			@Override
			public IHasControl apply(Composite from) throws Exception {
				CardConfig cardConfig = card.getCardConfig();
				IDetailsFactoryCallback callback = makeEditCallback(card);
				return cardConfig.detailFactory.makeDetail(from, card, cardConfig, key, value, callback);
			}

		});
		displayComments(card);
	}

	@Override
	public void displayHelpTextFor(final ICard card, final String key) {
		String cardType = card.cardType();
		String helpKey = "help." + cardType + "." + key;
		masterDetailSocial.createAndShowSocial(TextInBorder.makeTextWhenKeyMightNotExist(SWT.WRAP, cardConfig, cardType, helpKey));
	}

	private IDetailsFactoryCallback makeEditCallback(final ICard card) {
		IDetailsFactoryCallback callback = new IDetailsFactoryCallback() {

			@Override
			public void afterEdit(String url) {// reload
				ICallback.Utils.call(callbackToGotoUrlAndUpdateDetails, card.url());
			}

			@Override
			public void gotData(final Control control) {// /layout
			}

			@Override
			public void cardSelected(String cardUrl) {
				ICallback.Utils.call(callbackToGotoUrlAndUpdateDetails, cardUrl);
			}

			@Override
			public void updateDataStore(IMutableCardDataStore store, String url, String key, Object value) {
				store.put(url, Maps.stringObjectMap(key, value), this);
			}
		};
		return callback;
	}

	protected String makeRandomUUID() {
		return UUID.randomUUID().toString();
	}

	@Override
	public Control getControl() {
		return masterDetailSocial.getControl();
	}

	@Override
	public Future<String> processUrl(String feedType, String url) {
		masterDetailSocial.hideSocial();
		masterDetailSocial.setDetail(browser.getControl());
		return browser.processUrl(feedType, url);
	}

	@Override
	public PlayItem next() {
		masterDetailSocial.hideSocial();
		masterDetailSocial.setDetail(browser.getControl());
		return timeLine.next();
	}

	@Override
	public PlayItem previous() {
		masterDetailSocial.setDetail(browser.getControl());
		masterDetailSocial.hideSocial();
		return timeLine.previous();
	}

	@Override
	public Future<?> selectAndNext(String playListName) {
		masterDetailSocial.setDetail(browser.getControl());
		masterDetailSocial.hideSocial();
		return timeLine.selectAndNext(playListName);
	}

	@Override
	public void push(PlayItem newItem) {
		timeLine.push(newItem);
	}

	@Override
	public void addHistoryListener(IHistoryListener<PlayItem> listener) {
		timeLine.addHistoryListener(listener);
	}

	@Override
	public boolean hasNext() {
		return timeLine.hasNext();
	}

	@Override
	public boolean hasPrevious() {
		return timeLine.hasPrevious();
	}

	@Override
	public PlayItem getItem(int i) {
		return timeLine.getItem(i);
	}

	@Override
	public int size() {
		return timeLine.size();
	}

	@Override
	public void addCardListener(ICardChangedListener listener) {
		cardHolder.addCardChangedListener(listener);

	}

	@Override
	public Iterator<PlayItem> iterator() {
		return timeLine.iterator();
	}

	@Override
	public PlayItem last() {
		return timeLine.last();
	}

	@Override
	public Composite getComposite() {
		return masterDetailSocial.getComposite();
	}

	@Override
	public void displayComments(ICard card) {
		masterDetailSocial.showSocial();
		comments.showCommentsFor(card);
		masterDetailSocial.setSocial(comments.getControl());
	}

	@Override
	public void showContents() {
		final ICard card = cardHolder.getCard();
		fireListeners(new ICallback<IExplorerListener>() {
			@Override
			public void process(IExplorerListener t) throws Exception {
				t.showContents(card);
			}
		});
		if (card != null) {
			Table table = card.getTable();
			int selectionIndex = table.getSelectionIndex();
			if (selectionIndex != -1) {
				TableItem item = table.getItem(selectionIndex);
				Object key = item.getData();
				Map<String, Object> data = card.data();
				Object value = data.get(key);
				if (value instanceof Map<?, ?>) {
					displayCard(card.url() + "/" + key, new CardAndCollectionDataStoreAdapter() {
						@Override
						public void finished(ICardHolder cardHolder, String url, ICard card) {
							if (card.getControl().isDisposed())
								return;
							if (card.getTable().getItemCount() > 0) {
								card.getTable().select(0);
								card.getTable().notifyListeners(SWT.Selection, new Event());
							}
							Swts.layoutDump(cardHolder.getControl());
						}

					});
				}
			}
		}

	}

	private void fireListeners(ICallback<IExplorerListener> iCallback) {
		try {
			for (IExplorerListener listener : listeners)
				ICallback.Utils.call(iCallback, listener);
		} catch (Exception e) {
			e.printStackTrace();
			throw WrappedException.wrap(e);
		}

	}

	public static void main(String[] args) {
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacardForCardExplorer();
		final IServiceExecutor service = IServiceExecutor.Utils.defaultExecutor();
		try {
			final String rootUrl = "/softwareFm/data";
			final String firstUrl = "/softwareFm/data/org";

			Show.display(Explorer.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(Composite from) throws Exception {
					final ICardDataStore cardDataStore = ICardDataStore.Utils.repositoryCardDataStore(from, facard);
					ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
					final CardConfig cardConfig = ICollectionConfigurationFactory.Utils.softwareFmConfigurator().configure(from.getDisplay(), new CardConfig(cardFactory, cardDataStore));
					IMasterDetailSocial masterDetailSocial = new MasterDetailSocial(from, SWT.NULL);
					IExplorer explorer = new Explorer(cardConfig, rootUrl, masterDetailSocial, service, IPlayListGetter.Utils.noPlayListGetter());
					explorer.displayCard(firstUrl, new CardAndCollectionDataStoreAdapter());
					return masterDetailSocial.getComposite();
				}
			});
		} finally {
			facard.shutdown();
			service.shutdown();
		}
	}

	private final Random random = new Random();
	private final List<IExplorerListener> listeners = new CopyOnWriteArrayList<IExplorerListener>();

	@Override
	public void showRandomSnippetFor(String artifactUrl) {
		masterDetailSocial.setDetail(browser.getControl());
		masterDetailSocial.hideSocial();
		cardConfig.cardDataStore.processDataFor(artifactUrl + "/snippet", new ICardDataStoreCallback<Void>() {
			@Override
			public Void process(String url, Map<String, Object> result) throws Exception {
				List<String> keys = Lists.newList();
				for (Map.Entry<String, Object> entry : result.entrySet()) {
					if (entry.getValue() instanceof Map<?, ?>)
						keys.add(entry.getKey());
				}
				if (result.size() > 0) {
					String key = keys.get(random.nextInt(keys.size()));
					// TODO centralise this code
					String urlPattern = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, "snippet", CardConstants.cardContentUrl);
					String snippetUrl = MessageFormat.format(urlPattern, url, key);
					browser.processUrl(DisplayConstants.snippetFeedType, snippetUrl);
				}
				return null;
			}

			@Override
			public Void noData(String url) throws Exception {
				return null;
			}
		});

	}

	@Override
	public void showRandomContent(ICard card) {
		masterDetailSocial.hideSocial();
		masterDetailSocial.setDetail(browser.getControl());
		Map<String, Object> data = card.data();
		List<String> keys = Lists.newList();
		for (Map.Entry<String, Object> entry : data.entrySet()) {
			if (entry.getValue() instanceof Map<?, ?>)
				keys.add(entry.getKey());
		}
		if (data.size() > 0) {
			int index = random.nextInt(keys.size());
			card.getTable().select(index);
			card.getTable().notifyListeners(SWT.Selection, new Event());
		}

	}

	@Override
	public CardConfig getCardConfig() {
		return cardConfig;
	}

	@Override
	public void removeExplorerListener(IExplorerListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void addExplorerListener(IExplorerListener listener) {
		listeners.add(listener);
	}

	public IMasterDetailSocial getMasterDetailSocial() {
		return masterDetailSocial;
	}

	public BrowserAndNavBar getBrowser() {
		return browser;
	}

	@Override
	public void displayNotAJar() {
		masterDetailSocial.createAndShowMaster(TextInBorder.makeText(SWT.WRAP, cardConfig, CollectionConstants.fileNotAJarCardType, CollectionConstants.fileNotAJarText));
	}
}