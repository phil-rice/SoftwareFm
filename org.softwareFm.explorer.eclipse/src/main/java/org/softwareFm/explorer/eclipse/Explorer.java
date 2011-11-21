package org.softwareFm.explorer.eclipse;

import java.io.File;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.card.BasicCardConfigurator;
import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.card.IAddItemProcessor;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardChangedListener;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.card.ILineSelectedListener;
import org.softwareFm.card.card.RightClickCategoryResult;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardAndCollectionDataStoreAdapter;
import org.softwareFm.card.dataStore.IAfterEditCallback;
import org.softwareFm.card.dataStore.ICardDataStore;
import org.softwareFm.card.dataStore.ICardDataStoreCallback;
import org.softwareFm.card.dataStore.IMutableCardDataStore;
import org.softwareFm.card.details.IDetailsFactoryCallback;
import org.softwareFm.card.editors.IValueEditor;
import org.softwareFm.card.navigation.internal.NavNextHistoryPrevConfig;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.browser.IBrowserPart;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.display.timeline.IPlayListGetter;
import org.softwareFm.display.timeline.PlayItem;
import org.softwareFm.display.timeline.TimeLine;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.history.IHistoryListener;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.services.IServiceExecutor;
import org.softwareFm.utilities.strings.Strings;

public class Explorer implements IExplorer {

	private  final class AddItemProcessor implements IAddItemProcessor {
		private final IMasterDetailSocial masterDetailSocial;

		private AddItemProcessor(IMasterDetailSocial masterDetailSocial) {
			this.masterDetailSocial = masterDetailSocial;
		}

		@Override
		public void addCollectionItem(final RightClickCategoryResult result) {
			final ICard card = cardHolder.getCard();

			IValueEditor editor = masterDetailSocial.createDetail(new IFunction1<Composite, IValueEditor>() {
				@Override
				public IValueEditor apply(Composite from) throws Exception {
					return IValueEditor.Utils.textEditorWithLayout(from, card.cardConfig(), result.url, card.cardType(), result.collectionName, "", new IDetailsFactoryCallback() {
						private final IDetailsFactoryCallback callback = this;

						@Override
						public void updateDataStore(final IMutableCardDataStore store, String url, String key, final Object value) {
							updateStore(store, result, value, callback);
						}

						@Override
						public void afterEdit(String url) {
							ICallback.Utils.call(callbackToGotoUrlAndUpdateDetails, url);
						}

						@Override
						public void gotData(Control hasControl) {
						}

						@Override
						public void cardSelected(String cardUrl) {
						}

					}, TitleSpec.noTitleSpec(from.getBackground()));
				}
			}, false);
			masterDetailSocial.setDetail(editor.getControl());
		}

		@Override
		public void addNewArtifact() {
			unrecognisedJar.setFileAndDigest(null, null);
			masterDetailSocial.setMaster(unrecognisedJar.getControl());
		}
	}

	private UnrecognisedJar unrecognisedJar;
	private ICardHolder cardHolder;
	private ICallback<String> callbackToGotoUrlAndUpdateDetails;
	private final IMasterDetailSocial masterDetailSocial;
	private final CardConfig cardConfig;
	private BrowserAndNavBar browser;
	private TimeLine timeLine;
	private IHelpText helpText;

	public Explorer(final CardConfig cardConfigParam, final String rootUrl, final IMasterDetailSocial masterDetailSocial, final IServiceExecutor service, IPlayListGetter playListGetter) {
		this.cardConfig = cardConfigParam.withAddItemProcessor(makeAddItemProcessor(masterDetailSocial));
		this.masterDetailSocial = masterDetailSocial;
		helpText = masterDetailSocial.createSocial(new IFunction1<Composite, IHelpText>() {
			@Override
			public IHelpText apply(Composite from) throws Exception {
				return new HelpText(from);
			}
		}, true);
		callbackToGotoUrlAndUpdateDetails = new ICallback<String>() {
			@Override
			public void process(String url) throws Exception {
				masterDetailSocial.setMaster(cardHolder.getControl());
				cardConfig.cardCollectionsDataStore.processDataFor(cardHolder, cardConfig, url, new CardAndCollectionDataStoreAdapter() {
					@Override
					public void finished(ICardHolder cardHolder, String url, ICard card) {
						String key = findDefaultChild(card);
						showDetailForCardKey(masterDetailSocial, card, key, card.data().get(key));
					}
				});
			}
		};
		unrecognisedJar = masterDetailSocial.createMaster(new IFunction1<Composite, UnrecognisedJar>() {
			@Override
			public UnrecognisedJar apply(final Composite from) throws Exception {
				UnrecognisedJar unrecognisedJar = new UnrecognisedJar(from, SWT.NULL, cardConfig, new ICallback<String>() {
					@Override
					public void process(String artifactUrl) throws Exception {
						displayCard(artifactUrl);
					}
				});
				unrecognisedJar.getComposite().setLayout(new UnrecognisedJar.UnrecognisedJarLayout());
				return unrecognisedJar;
			}
		}, true);
		cardHolder = masterDetailSocial.createMaster(new IFunction1<Composite, ICardHolder>() {
			@Override
			public ICardHolder apply(Composite from) throws Exception {
				ICardHolder cardHolder = ICardHolder.Utils.cardHolderWithLayout(from, "loading", "Some title", cardConfig, rootUrl, callbackToGotoUrlAndUpdateDetails);
				return cardHolder;
			}
		}, true);
		cardHolder.addLineSelectedListener(new ILineSelectedListener() {
			@Override
			public void selected(final ICard card, final String key, final Object value) {
				showDetailForCardKey(masterDetailSocial, card, key, value);
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

	}

	@Override
	public IBrowserPart register(String feedType, IFunction1<Composite, IBrowserPart> feedPostProcessor) {
		return browser.register(feedType, feedPostProcessor);
	}

	@Override
	public void displayCard(String url) {
		masterDetailSocial.setMaster(cardHolder.getControl());
		cardConfig.cardCollectionsDataStore.processDataFor(cardHolder, cardConfig, url, new CardAndCollectionDataStoreAdapter());
	}

	@Override
	public void displayUnrecognisedJar(File file, String digest) {
		unrecognisedJar.setFileAndDigest(file, digest);
		masterDetailSocial.setMaster(unrecognisedJar.getControl());
		unrecognisedJar.getControl().redraw();
	}

	@Override
	public void displayComments(final String url) {
		masterDetailSocial.createAndShowSocial(Swts.styledTextFn(url, SWT.WRAP));
		masterDetailSocial.showSocial();
	}

	private String findDefaultChild(ICard card) {
		String result = Functions.call(card.cardConfig().defaultChildFn, card);
		return result;
	}

	private void showDetailForCardKey(IMasterDetailSocial masterDetailSocial, final ICard card, final String key, final Object value) {
		masterDetailSocial.showSocial();
		masterDetailSocial.createAndShowDetail(new IFunction1<Composite, IHasControl>() {
			@Override
			public IHasControl apply(Composite from) throws Exception {
				CardConfig cardConfig = card.cardConfig();
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
				return cardConfig.detailFactory.makeDetail(from, card, cardConfig, key, value, callback);
			}
		});
		String cardType = card.cardType();
		String helpKey = "help." + cardType + "." + key;
		String help = IResourceGetter.Utils.getOrNull(cardConfig.resourceGetterFn, cardType, helpKey);
		masterDetailSocial.setSocial(helpText.getControl());
		helpText.setText(Strings.nullSafeToString(help));
		if (help == null)
//			masterDetailSocial.setSocial(null);
			;
		else {
		}
	}

	/** This is the bit that configures then acts on the right click that adds folders/groups/collections */
	private IAddItemProcessor makeAddItemProcessor(final IMasterDetailSocial masterDetailSocial) {
		IAddItemProcessor itemProcessor = new AddItemProcessor(masterDetailSocial);
		return itemProcessor;
	}

	private void updateStore(final IMutableCardDataStore store, final RightClickCategoryResult result, final Object value, final IAfterEditCallback afterEditCallback) {
		final String editorResult = Strings.stringToUrlSegment(Strings.nullSafeToString(value));
		switch (result.itemType) {
		case ROOT_COLLECTION:
			createNewItem(result, store, editorResult, afterEditCallback);
			break;
		case IS_COLLECTION:
			store.processDataFor(result.collectionUrl(), new ICardDataStoreCallback<Void>() {
				@Override
				public Void process(String url, Map<String, Object> data) throws Exception {
					createNewItem(result, store, editorResult, afterEditCallback);
					return null;
				}

				@Override
				public Void noData(final String url) throws Exception {
					final Map<String, Object> newData = Maps.stringObjectMap(CardConstants.slingResourceType, CardConstants.collection);
					store.put(url, newData, new IAfterEditCallback() {
						@Override
						public void afterEdit(String url) {
							try {
								process(url, newData);
							} catch (Exception e) {
								throw WrappedException.wrap(e);
							}
						}
					});
					return null;
				}
			});
			break;
		default:
			throw new IllegalStateException(result.toString());
		}
	}

	private void createNewItem(final RightClickCategoryResult result, final IMutableCardDataStore store, String newItemName, IAfterEditCallback afterEditCallback) {
		String cardUrl = IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, result.collectionName, CardConstants.cardNameUrlKey);
		String cardName = IResourceGetter.Utils.getOrNull(cardConfig.resourceGetterFn, result.collectionName, CardConstants.cardNameFieldKey);
		String itemName = cardUrl == null ? newItemName : MessageFormat.format(cardUrl, Strings.forUrl(newItemName), UUID.randomUUID());
		String fullUrl = result.itemUrl(itemName);
		Map<String, Object> baseData = Maps.stringObjectMap(CardConstants.slingResourceType, result.collectionName);
		if (cardName != null)
			baseData.put(cardName, newItemName);
		store.put(fullUrl, baseData, afterEditCallback);
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
					final CardConfig cardConfig = new BasicCardConfigurator().configure(from.getDisplay(), new CardConfig(cardFactory, cardDataStore));
					IMasterDetailSocial masterDetailSocial = new MasterDetailSocial(from, SWT.NULL);
					IExplorer explorer = new Explorer(cardConfig, rootUrl, masterDetailSocial, service, IPlayListGetter.Utils.noPlayListGetter());
					explorer.displayCard(firstUrl);
					return masterDetailSocial.getComposite();
				}
			});
		} finally {
			facard.shutdown();
			service.shutdown();
		}
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

}
