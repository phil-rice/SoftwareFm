/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.explorer.internal;

import java.io.File;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.history.IHistoryListener;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.resources.IResourceGetter;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.comments.ICommentDefn;
import org.softwareFm.eclipse.constants.CommentConstants;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IUserMembershipReader;
import org.softwareFm.swt.browser.IBrowserPart;
import org.softwareFm.swt.card.ICard;
import org.softwareFm.swt.card.ICardChangedListener;
import org.softwareFm.swt.card.ICardData;
import org.softwareFm.swt.card.ICardHolder;
import org.softwareFm.swt.card.ILineSelectedListener;
import org.softwareFm.swt.card.RightClickCategoryResult;
import org.softwareFm.swt.card.composites.CardShapedHolder;
import org.softwareFm.swt.card.composites.TextInBorder;
import org.softwareFm.swt.comments.Comments;
import org.softwareFm.swt.comments.CommentsEditor;
import org.softwareFm.swt.comments.ICommentsCallback;
import org.softwareFm.swt.comments.ICommentsEditorCallback;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.constants.CollectionConstants;
import org.softwareFm.swt.constants.DisplayConstants;
import org.softwareFm.swt.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.swt.dataStore.CardDataStoreCallbackAdapter;
import org.softwareFm.swt.dataStore.IAfterEditCallback;
import org.softwareFm.swt.dataStore.ICardAndCollectionDataStoreVisitor;
import org.softwareFm.swt.dataStore.ICardDataStoreCallback;
import org.softwareFm.swt.dataStore.IMutableCardDataStore;
import org.softwareFm.swt.details.IDetailsFactoryCallback;
import org.softwareFm.swt.editors.ICardEditorCallback;
import org.softwareFm.swt.editors.IEditorDetailAdder;
import org.softwareFm.swt.editors.IValueEditor;
import org.softwareFm.swt.explorer.BrowserAndNavBar;
import org.softwareFm.swt.explorer.IExplorer;
import org.softwareFm.swt.explorer.IExplorerListener;
import org.softwareFm.swt.explorer.IMasterDetailSocial;
import org.softwareFm.swt.explorer.IShowMyData;
import org.softwareFm.swt.explorer.IShowMyGroups;
import org.softwareFm.swt.explorer.IShowMyPeople;
import org.softwareFm.swt.explorer.IUserDataManager;
import org.softwareFm.swt.mySoftwareFm.ILoginStrategy;
import org.softwareFm.swt.navigation.internal.NavNextHistoryPrevConfig;
import org.softwareFm.swt.swt.Swts;
import org.softwareFm.swt.timeline.IPlayListGetter;
import org.softwareFm.swt.timeline.PlayItem;
import org.softwareFm.swt.timeline.TimeLine;
import org.softwareFm.swt.title.TitleSpec;
import org.softwareFm.swt.unrecognisedJar.GroupidArtifactVersion;
import org.softwareFm.swt.unrecognisedJar.GuessArtifactAndVersionDetails;
import org.softwareFm.swt.unrecognisedJar.UnrecognisedJar;
import org.softwareFm.swt.unrecognisedJar.UnrecognisedJarData;

public class Explorer implements IExplorer {

	public ICardHolder cardHolder;
	private ICallback<String> callbackToGotoUrlAndUpdateDetails;
	private final IMasterDetailSocial masterDetailSocial;
	private final CardConfig cardConfig;
	private BrowserAndNavBar browser;
	private TimeLine timeLine;
	private Comments comments;
	private MySoftwareFm mySoftwareFm;
	private final IShowMyPeople showMyPeople;

	public Explorer(final CardConfig cardConfig, final List<String> rootUrls, final IMasterDetailSocial masterDetailSocial, final IServiceExecutor service, final IUserReader userReader, final IUserMembershipReader userMembershipReader, final IGroupsReader groupsReader, IPlayListGetter playListGetter, final ILoginStrategy loginStrategy, final IShowMyData showMyData, final IShowMyGroups showMyGroups, final IShowMyPeople showMyPeople, final IUserDataManager userDataManager) {
		this.cardConfig = cardConfig;
		this.masterDetailSocial = masterDetailSocial;
		this.showMyPeople = showMyPeople;
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
				ICardHolder cardHolder = ICardHolder.Utils.cardHolderWithLayout(from, cardConfig, rootUrls, callbackToGotoUrlAndUpdateDetails);
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
						masterDetailSocial.putSocialOverDetail();
						masterDetailSocial.createAndShowDetail(TextInBorder.makeTextFromString(SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL, cardConfig, CollectionConstants.comment, title, text));

					}
				}, new Runnable() {
					@Override
					public void run() {
						final String baseUrl = cardHolder.getCard().url(); // the url when the button was pushed
						masterDetailSocial.createAndShowDetail(new IFunction1<Composite, CommentsEditor>() {
							@Override
							public CommentsEditor apply(Composite from) throws Exception {
								final UserData userData = mySoftwareFm.getUserData();
								if (userData.softwareFmId == null)
									throw new IllegalStateException("Need to be logged in");
								String title = CommentConstants.editorTitle;
								List<Map<String, Object>> groupData = userMembershipReader.walkGroupsFor(userData.softwareFmId, userData.crypto);
								List<String> groupNames = getGroupNames(groupData);
								return new CommentsEditor(from, cardConfig, title, "", groupNames, new ICommentsEditorCallback() {
									@Override
									public void youComment(String text) {
										sendComment(ICommentDefn.Utils.myInitial(userData.softwareFmId, userData.crypto, baseUrl), text);
										displayCardAgain(baseUrl);
									}

									protected void displayCardAgain(final String baseUrl) {
										masterDetailSocial.setDetail(null);
										displayCard(baseUrl, new CardAndCollectionDataStoreAdapter() {
											@Override
											public void finished(ICardHolder cardHolder, String url, ICard card) throws Exception {
												String key = findDefaultChild(card);
												showDetailForCardKey(card, key, card.data().get(key));
											}
										});
									}

									@Override
									public void groupComment(int groupIndex, String text) {
										System.out.println("group: " + groupIndex);
										displayCardAgain(baseUrl);
									}

									@Override
									public void everyoneComment(String text) {
										System.out.println("everyone: ");
										displayCardAgain(baseUrl);
									}

									@Override
									public void cancel() {
										System.out.println("cancel");
										displayCardAgain(baseUrl);
									}
								});
							}

							@SuppressWarnings({ "rawtypes", "unchecked" })
							private List<String> getGroupNames(Iterable<Map<String, Object>> groupData) {
								UserData userData = mySoftwareFm.getUserData();
								if (userData.softwareFmId == null)
									return Collections.emptyList();
								List result = Lists.map(groupData, new IFunction1<Map<String, Object>, String>() {
									@Override
									public String apply(Map<String, Object> from) throws Exception {
										String groupId = (String) from.get(GroupConstants.groupIdKey);
										String groupCrypto = (String) from.get(GroupConstants.groupCryptoKey);
										if (groupId == null || groupCrypto == null)
											throw new NullPointerException(from.toString());
										return groupsReader.getGroupProperty(groupId, groupCrypto, GroupConstants.groupNameKey);
									}
								});
								return result;
							}
						});
					}
				});
			}
		}, true);
		mySoftwareFm = masterDetailSocial.createMaster(new IFunction1<Composite, MySoftwareFm>() {
			@Override
			public MySoftwareFm apply(Composite from) throws Exception {
				MySoftwareFm mySoftwareFm = new MySoftwareFm(from, cardConfig, loginStrategy, showMyData, showMyGroups, userReader, userDataManager);
				mySoftwareFm.start();
				return mySoftwareFm;
			}
		}, true);
	}

	@Override
	public void showMySoftwareFm() {
		Composite composite = mySoftwareFm.getComposite();
		masterDetailSocial.setMaster(composite);
		mySoftwareFm.forceFocus();
	}

	public void dispose() {
		masterDetailSocial.dispose();
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
	public void showPeople(String groupId, String artifactId) {
		showMyPeople.showMyPeople(getUserData(), groupId, artifactId);
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
						addCollectionItem(result, collectionName, cardData, new IAfterEditCallback() {
							@Override
							public void afterEdit(String url) {
								timeLine.clear();
								final String key = Strings.lastSegment(url, "/");
								final String collectionUrl = Strings.allButLastSegment(url, "/");
								displayAndSelectItemWithKey(collectionUrl, key, new ICallback<IExplorerListener>() {
									@Override
									public void process(IExplorerListener t) throws Exception {
										t.collectionItemAdded(collectionUrl, key);
									}
								});
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

	private void addCollectionItem(final RightClickCategoryResult result, final String collectionName, ICardData cardData, final IAfterEditCallback afterEdit) {
		IMutableCardDataStore store = cardConfig.cardDataStore;
		assert result.collectionName.equals(collectionName);// TODO do we really need this parameter?
		Map<String, Object> newData = Maps.with(cardData.data(), CollectionConstants.createdTimeKey, System.currentTimeMillis());
		String cardUrl = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, collectionName, CardConstants.cardNameUrlKey);
		String cardNameKey = IResourceGetter.Utils.getOrNull(cardConfig.resourceGetterFn, collectionName, CardConstants.cardNameFieldKey);
		String cardName = (String) newData.get(cardNameKey);
		String itemUrlFragment = MessageFormat.format(cardUrl, Strings.forUrl(cardName), makeRandomUUID());

		IMutableCardDataStore.Utils.addCollectionItem(store, result, itemUrlFragment, newData, new IAfterEditCallback() {
			@Override
			public void afterEdit(String url) {
				afterEdit.afterEdit(url);
			}

		});
	}

	private void displayAndSelectItemWithKey(String url, final String key, final ICallback<IExplorerListener> callback) {
		displayCard(url, new CardAndCollectionDataStoreAdapter() {
			@Override
			public void finished(ICardHolder cardHolder, String url, ICard card) throws Exception {
				try {
					if (key != null) {
						Table table = card.getTable();
						for (int i = 0; i < table.getItemCount(); i++) {
							TableItem item = table.getItem(i);
							if (key.equals(item.getData())) {
								table.select(i);
								table.notifyListeners(SWT.Selection, new Event());
								return;
							}
						}
					}
				} finally {
					fireListeners(callback);
				}
			}
		});
	}

	//
	// private String getKey() {
	// ICard card = cardHolder.getCard();
	// if (card != null) {
	// int index = card.getTable().getSelectionIndex();
	// if (index != -1) {
	// TableItem item = card.getTable().getItem(index);
	// String key = (String) item.getData();
	// return key;
	// }
	// }
	// return null;
	//
	// }

	@Override
	public void editSnippet(final ICard card, final String key) {
		final IMutableCardDataStore store = card.getCardConfig().cardDataStore;
		masterDetailSocial.showSocial();
		masterDetailSocial.createAndShowDetail(new IFunction1<Composite, IValueEditor>() {
			@Override
			public IValueEditor apply(Composite from) throws Exception {
				final String url = card.url() + "/" + key;
				@SuppressWarnings("unchecked")
				Map<String, Object> data = (Map<String, Object>) card.data().get(key);
				return IValueEditor.Utils.snippetEditorWithLayout(from, cardConfig, "Snippet", url, data, new ICardEditorCallback() {
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
								masterDetailSocial.hideSocial();
								masterDetailSocial.setDetail(browser.getControl());
							}
						};
						store.put(url, data, callback);
					}

					@Override
					public void cancel(ICardData cardData) {
						masterDetailSocial.setDetail(null);
						displayCard(card.url(), new CardAndCollectionDataStoreAdapter());
					}

					@Override
					public boolean canOk(Map<String, Object> data) {
						return snippetOk(data);
					}
				});
			}
		});
	}

	private boolean snippetOk(Map<String, Object> data) {
		String title = Strings.nullSafeToString(data.get("title"));
		String description = Strings.nullSafeToString(data.get("description"));
		String content = Strings.nullSafeToString(data.get("content"));
		boolean titleOk = title.length() > 0;
		boolean contentOk = content.length() > 0;
		boolean descriptionOk = !description.startsWith("<");
		return titleOk && contentOk && descriptionOk;
	}

	@Override
	public void showAddSnippetEditor(final ICard card) {
		final IMutableCardDataStore store = card.getCardConfig().cardDataStore;
		final String lastSegment = Strings.lastSegment(card.url(), "/");
		masterDetailSocial.showSocial();
		masterDetailSocial.createAndShowDetail(new IFunction1<Composite, IValueEditor>() {
			@Override
			public IValueEditor apply(Composite from) throws Exception {
				return IValueEditor.Utils.snippetEditorWithLayout(from, cardConfig, "Snippet", card.url(), Maps.stringObjectMap(), new ICardEditorCallback() {
					@Override
					public void ok(final ICardData cardData) {
						final String fragment = UUID.randomUUID().toString();
						String url = cardData.url();
						final Map<String, Object> data = cardData.data();
						final IAfterEditCallback callback = new IAfterEditCallback() {
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
								masterDetailSocial.hideSocial();
								masterDetailSocial.setDetail(browser.getControl());

							}
						};
						cardConfig.cardDataStore.processDataFor(url, new ICardDataStoreCallback<Void>() {
							@Override
							public Void process(String url, Map<String, Object> result) throws Exception {
								Map<String, Object> cleanedData = Maps.withOnly(data, "title", "description", "content", CommonConstants.typeTag);
								if (lastSegment.equals(CardConstants.snippet)) {
									IMutableCardDataStore.Utils.addCollectionItemToCollection(store, url, CardConstants.snippet, fragment, cleanedData, callback);
								} else
									IMutableCardDataStore.Utils.addCollectionItemToBase(store, url, CardConstants.snippet, fragment, cleanedData, callback);
								return null;
							}

							@Override
							public Void noData(String url) throws Exception {
								cardConfig.cardDataStore.makeRepo(url, new IAfterEditCallback() {
									@Override
									public void afterEdit(String url) {
										cardConfig.cardDataStore.put(url, Maps.stringObjectMap(CardConstants.collection), new IAfterEditCallback() {
											@Override
											public void afterEdit(String url) {
												try {
													process(url, Maps.emptyStringObjectMap());
												} catch (Exception e) {
													throw WrappedException.wrap(e);
												}
											}
										});
									}
								});
								return null;
							}
						});
					}

					@Override
					public void cancel(ICardData cardData) {
						masterDetailSocial.setDetail(null);
						displayCard(cardData.url(), new CardAndCollectionDataStoreAdapter());
					}

					@Override
					public boolean canOk(Map<String, Object> data) {
						return snippetOk(data);
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
		masterDetailSocial.putDetailOverSocial();
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
				comments.showCommentsFor(card);
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
		masterDetailSocial.putDetailOverSocial();
		displayHelpKey(CollectionConstants.jarNotRecognisedCardType, CollectionConstants.helpUnrecognisedPleaseAddText);
		final GuessArtifactAndVersionDetails guesser = new GuessArtifactAndVersionDetails();
		final String guessedVersion = guesser.guessVersion(file);
		if (file.getName().equals("rt.jar") && guessedVersion.length() > 0) {
			displayUnrecognisedRtJar(file, digest, projectName, guessedVersion);
			return;
		}
		masterDetailSocial.createAndShowMaster(TextInBorder.makeTextWithClick(SWT.WRAP | SWT.READ_ONLY, cardConfig, new Runnable() {
			@Override
			public void run() {
				displayHelpKey(CollectionConstants.jarNotRecognisedCardType, CollectionConstants.helpUnrecognisedThankYouText);
				final Map<String, Object> startData = guessDetailsForUnrecognisedJar(file);
				addUnrecognisedJar(file, digest, projectName, startData);
				final TitleSpec titleSpec = Functions.call(cardConfig.titleSpecFn, ICardData.Utils.create(cardConfig, CollectionConstants.jarNotRecognisedCardType, "", Maps.emptyStringObjectMap()));
				IFunction1<Composite, CardShapedHolder<UnrecognisedJar>> text = new IFunction1<Composite, CardShapedHolder<UnrecognisedJar>>() {
					@Override
					public CardShapedHolder<UnrecognisedJar> apply(Composite from) throws Exception {
						String title = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, CollectionConstants.jarNotRecognisedCardType, CollectionConstants.jarNotRecognisedTitle);
						return new CardShapedHolder<UnrecognisedJar>(from, cardConfig, titleSpec, Swts.labelFn(title), new IFunction1<Composite, UnrecognisedJar>() {
							@Override
							public UnrecognisedJar apply(Composite from) throws Exception {
								UnrecognisedJarData jarData = UnrecognisedJarData.forTests(projectName, file);
								UnrecognisedJar unrecognisedJar = new UnrecognisedJar(from, getCardConfig(), jarData, new ICallback<GroupidArtifactVersion>() {
									@Override
									public void process(GroupidArtifactVersion t) throws Exception {
										Map<String, Object> data = Maps.stringObjectMap(//
												SoftwareFmConstants.groupId, t.groupId,//
												SoftwareFmConstants.artifactId, t.artifactId,//
												SoftwareFmConstants.version, t.version);
										addUnrecognisedJar(file, digest, projectName, data);
									}
								});
								return unrecognisedJar;
							}
						});
					}
				};
				CardShapedHolder<UnrecognisedJar> holder = masterDetailSocial.createAndShowMaster(text);
				UnrecognisedJar unrecognisedJar = holder.getBody();

				searchForJar(unrecognisedJar, file);
			}

			private void searchForJar(final UnrecognisedJar unrecognisedJar, final File file) {
				IUrlGenerator urlGenerator = cardConfig.urlGeneratorMap.get(CardConstants.jarNameUrlKey);
				String jarName = file.getName().equals("rt.jar") ? "rt" : guesser.guessArtifactName(file);
				String url = urlGenerator.findUrlFor(Maps.stringObjectMap(CollectionConstants.jarStem, jarName));
				cardConfig.cardDataStore.processDataFor(url, new CardDataStoreCallbackAdapter<Void>() {
					@SuppressWarnings("unchecked")
					@Override
					public Void process(String url, Map<String, Object> result) throws Exception {
						List<GroupidArtifactVersion> gavs = Lists.newList();
						for (Entry<String, Object> entry : result.entrySet())
							if (entry.getValue() instanceof Map<?, ?>) {
								Map<String, Object> map = (Map<String, Object>) entry.getValue();
								assert map.get(CardConstants.slingResourceType).equals(CardConstants.jarName) : "Entry: " + entry + "\nMap: " + map;
								GroupidArtifactVersion gav = new GroupidArtifactVersion(//
										Strings.nullSafeToString(map.get(CardConstants.group)), //
										Strings.nullSafeToString(map.get(CardConstants.artifact)), //
										guessedVersion);
								gavs.add(gav);
							}
						unrecognisedJar.populate(gavs);
						return null;
					}
				});
			}

			private Map<String, Object> guessDetailsForUnrecognisedJar(final File file) {
				GuessArtifactAndVersionDetails guesser = new GuessArtifactAndVersionDetails();
				final Map<String, Object> startData = Maps.stringObjectMap(//
						SoftwareFmConstants.groupId, file.getName().equals("rt.jar") ? "sun.jdk" : "Please specify the group id",//
						SoftwareFmConstants.artifactId, guesser.guessArtifactName(file),//
						SoftwareFmConstants.version, guesser.guessVersion(file));
				return startData;
			}
		}, CollectionConstants.jarNotRecognisedCardType, CollectionConstants.jarNotRecognisedTitle, CollectionConstants.jarNotRecognisedText, file, file.getName(), projectName));
	}

	private void displayHelpKey(String cardType, String key) {
		displayHelpText(cardType, IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, cardType, key));
	}

	private void displayUnrecognisedRtJar(final File file, final String digest, final String projectName, final String versionNo) {
		masterDetailSocial.createAndShowMaster(TextInBorder.makeTextWithClick(SWT.WRAP | SWT.READ_ONLY, cardConfig, new Runnable() {
			@Override
			public void run() {

				importJar(digest, "sun.jdk", "runtime", versionNo, file);
			}
		}, CollectionConstants.jarNotRecognisedCardType, CollectionConstants.jarRtNotRecognisedTitle, CollectionConstants.jarRtNotRecognisedText, file, file.getName(), projectName, versionNo));
	}

	private void addUnrecognisedJar(final File file, final String digest, final String projectName, final Map<String, Object> startData) {
		masterDetailSocial.showSocial();
		masterDetailSocial.putDetailOverSocial();
		masterDetailSocial.createAndShowDetail(new IFunction1<Composite, IValueEditor>() {
			@Override
			public IValueEditor apply(Composite from) throws Exception {
				String jarTitle = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, CollectionConstants.jarNotRecognisedCardType, CollectionConstants.jarNotRecognisedTitle);
				return IValueEditor.Utils.cardEditorWithLayout(from, cardConfig, jarTitle, CollectionConstants.jarNotRecognisedCardType, "", startData, new ICardEditorCallback() {
					@Override
					public void ok(ICardData cardData) {
						masterDetailSocial.setDetail(null);
						String groupId = (String) cardData.data().get(SoftwareFmConstants.groupId);
						String artifactId = (String) cardData.data().get(SoftwareFmConstants.artifactId);
						String version = (String) cardData.data().get(SoftwareFmConstants.version);
						importJar(digest, groupId, artifactId, version, file);
					}

					@Override
					public void cancel(ICardData cardData) {
						masterDetailSocial.setDetail(null);
						displayUnrecognisedJar(file, digest, projectName);
					}

					@Override
					public boolean canOk(Map<String, Object> data) {
						String groupId = Strings.nullSafeToString(data.get(SoftwareFmConstants.groupId));
						String artifactId = Strings.nullSafeToString(data.get(SoftwareFmConstants.artifactId));
						String version = Strings.nullSafeToString(data.get(SoftwareFmConstants.version));

						boolean groupValidated = Strings.isUrlFriendly(groupId);
						boolean artifactValidated = Strings.isUrlFriendly(artifactId);
						boolean versionValidated = Strings.isUrlFriendly(version);
						return groupValidated && artifactValidated && versionValidated;
					}
				});
			}
		});
	}

	private void importJar(final String digest, String groupId, String artifactId, String version, File file) {
		masterDetailSocial.createAndShowMaster(TextInBorder.makeText(SWT.WRAP | SWT.READ_ONLY, cardConfig, //
				CollectionConstants.jarNotRecognisedCardType, CollectionConstants.jarImportingTitle, CollectionConstants.jarImportingText,//
				groupId, artifactId, version));
		String jarStem = new GuessArtifactAndVersionDetails().guessArtifactName(file);
		new NewJarImporter(cardConfig, CardConstants.manuallyAdded, digest, groupId, artifactId, version, jarStem).processImport(new ICallback<String>() {
			@Override
			public void process(String url) throws Exception {
				displayCard(url, new CardAndCollectionDataStoreAdapter());
			}
		});
	}

	@Override
	public void displayHelpText(String cardType, final String text) {
		masterDetailSocial.createAndShowSocial(TextInBorder.makeTextFromString(SWT.WRAP | SWT.READ_ONLY, cardConfig, cardType, "Help", text));
		masterDetailSocial.showSocial();
	}

	private String findDefaultChild(ICard card) {
		String result = Functions.call(card.getCardConfig().defaultChildFn, card);
		return result;
	}

	@SuppressWarnings("unchecked")
	private void browseDetailForCardKey(ICard card, String key, Object value) {
		masterDetailSocial.hideSocial();
		masterDetailSocial.putDetailOverSocial();

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
		masterDetailSocial.putDetailOverSocial();
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
		masterDetailSocial.createAndShowSocial(TextInBorder.makeTextWhenKeyMightNotExist(SWT.WRAP, cardConfig, cardType, "", helpKey));
	}

	private IDetailsFactoryCallback makeEditCallback(final ICard card) {
		IDetailsFactoryCallback callback = new IDetailsFactoryCallback() {

			@Override
			public void afterEdit(String url) {// reload
				ICallback.Utils.call(callbackToGotoUrlAndUpdateDetails, card.url());
				timeLine.clear();
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
	public void clear() {
		timeLine.clear();
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
		comments.showCommentsFor(card);
		masterDetailSocial.showSocial();
		masterDetailSocial.setSocial(comments.getControl());
	}

	@Override
	public void showContents() {
		masterDetailSocial.putDetailOverSocial();
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
		masterDetailSocial.createAndShowMaster(TextInBorder.makeText(SWT.WRAP, cardConfig, CollectionConstants.fileNotAJarCardType, CollectionConstants.fileNotAJarTitle, CollectionConstants.fileNotAJarText));
	}

	public Comments getComments() {
		return comments;
	}

	@Override
	public void clearCaches() {
		cardConfig.cardDataStore.clearCaches();
	}

	@Override
	public ICardHolder getCardHolder() {
		return cardHolder;
	}

	private final AtomicInteger count = new AtomicInteger();

	@Override
	public void showDebug(String ripperResult) {
		String text = "This is a debug screen.\nCount: " + count.incrementAndGet() + "\n" + ripperResult;
		masterDetailSocial.createAndShowMaster(Swts.styledTextFn(text, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL));

	}

	@Override
	public UserData getUserData() {
		return mySoftwareFm.getUserData();
	}
}