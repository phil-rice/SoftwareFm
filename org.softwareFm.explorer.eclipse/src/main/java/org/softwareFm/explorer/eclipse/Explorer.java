package org.softwareFm.explorer.eclipse;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardAndCollectionDataStoreVisitorMonitored;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.IAddItemProcessor;
import org.softwareFm.card.api.IAfterEditCallback;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardChangedListener;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardHolder;
import org.softwareFm.card.api.IDetailsFactoryCallback;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.IMutableCardDataStore;
import org.softwareFm.card.api.RightClickCategoryResult;
import org.softwareFm.card.api.RightClickCategoryResult.Type;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.editors.TextEditor;
import org.softwareFm.card.internal.BasicCardConfigurator;
import org.softwareFm.card.internal.CardDataStoreForRepository;
import org.softwareFm.card.internal.CardHolder;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.strings.Strings;

public class Explorer implements IHasComposite, IExplorer {

	private final MasterDetail masterDetail;
	private UnrecognisedJar unrecognisedJar;
	private CardHolder cardHolder;
	private ICallback<String> callbackToGotoUrl;

	public Explorer(Composite parent, int style, final CardConfig cardConfig,  final String rootUrl) {
		callbackToGotoUrl = new ICallback<String>() {
			@Override
			public void process(String url) throws Exception {
				masterDetail.setMaster(cardHolder.getControl());
				cardConfig.cardCollectionsDataStore.processDataFor(cardHolder, cardConfig, url, CardAndCollectionDataStoreVisitorMonitored.Utils.sysout());
			}
		};

		masterDetail = new MasterDetail(parent, style);

		unrecognisedJar = masterDetail.createMaster(new IFunction1<Composite, UnrecognisedJar>() {
			@Override
			public UnrecognisedJar apply(final Composite from) throws Exception {
				return new UnrecognisedJar(from, SWT.NULL, cardConfig);
			}
		}, true);
		cardHolder = masterDetail.createMaster(new IFunction1<Composite, CardHolder>() {
			@Override
			public CardHolder apply(Composite from) throws Exception {
				return new CardHolder(from, "loading", "Some title", cardConfig, rootUrl, callbackToGotoUrl);
			}
		}, true);
		cardHolder.setAddItemProcessor(makeAddItemProcessor(cardConfig));
		cardHolder.addLineSelectedListener(new ILineSelectedListener() {
			@Override
			public void selected(final ICard card, final String key, final Object value) {
				showDetailForCardKey(card, key, value);
			}

		});
		cardHolder.addCardChangedListener(new ICardChangedListener() {//probably want to change this, so that we have a listener when the card has finished getting data, that way we won't have to show the default child multiple times
			@Override
			public void cardChanged(ICardHolder cardHolder, ICard card) {
				String key = findDefaultChild(card);
				showDetailForCardKey(card, key, card.data().get(key));
			}

			@Override
			public void valueChanged(ICard card, String key, Object newValue) {
				String defaultChild = findDefaultChild(card);
				if (defaultChild != null && defaultChild.equals(key))
					showDetailForCardKey(card, key, card.data().get(key));
			}
		});
	}

	private String findDefaultChild(ICard card) {
		String result = Functions.call(card.cardConfig().defaultChildFn, card);
		return result;
	}

	private void showDetailForCardKey(final ICard card, final String key, final Object value) {
		masterDetail.createAndShowDetail(new IFunction1<Composite, IHasControl>() {
			@Override
			public IHasControl apply(Composite from) throws Exception {
				CardConfig cardConfig = card.cardConfig();
				IDetailsFactoryCallback callback = new IDetailsFactoryCallback() {

					@Override
					public void afterEdit(String url) {// reload
						ICallback.Utils.call(callbackToGotoUrl, card.url());
					}

					@Override
					public void gotData(final Control control) {// /layout
					}

					@Override
					public void cardSelected(ICard card) {
						ICallback.Utils.call(callbackToGotoUrl, card.url());
					}

					@Override
					public void updateDataStore(IMutableCardDataStore store, String url, String key, Object value) {
						store.put(url, Maps.stringObjectMap(key, value), this);
					}
				};
				return cardConfig.detailFactory.makeDetail(from, card, cardConfig, key, value, callback);
			}

		});
	}

	/** This is the bit that configures then acts on the right click that adds folders/groups/collections */
	private IAddItemProcessor makeAddItemProcessor(final CardConfig cardConfig) {
		IAddItemProcessor itemProcessor = new IAddItemProcessor() {
			@Override
			public void process(final RightClickCategoryResult result) {
				System.out.println("In add item processor with " + result + " and card: " + cardHolder.getCard());

				TextEditor editor = masterDetail.createDetail(new IFunction1<Composite, TextEditor>() {
					@Override
					public TextEditor apply(Composite from) throws Exception {
						return new TextEditor(from, cardConfig, result.url, result.collectionName, "", new IDetailsFactoryCallback() {
							private final IDetailsFactoryCallback callback = this;

							@Override
							public void updateDataStore(final IMutableCardDataStore store, String url, String key, final Object value) {
								System.out.println("Updateing data store: " + value);
								updateStore(store, result, value, callback);
							}

							@Override
							public void afterEdit(String url) {
								System.out.println("After edit");
								ICallback.Utils.call(callbackToGotoUrl, url);
							}

							@Override
							public void gotData(Control hasControl) {
							}

							@Override
							public void cardSelected(ICard card) {
							}

						}, TitleSpec.noTitleSpec(from.getBackground()));
					}
				}, false);
				masterDetail.setDetail(editor.getControl());
			}
		};
		return itemProcessor;
	}

	private void updateStore(final IMutableCardDataStore store, final RightClickCategoryResult result, final Object value, final IAfterEditCallback afterEditCallback) {
		final String editorResult = Strings.stringToUrlSegment(Strings.nullSafeToString(value));
		if (result.itemType == Type.ROOT_COLLECTION) {
			createNewItem(result, store, editorResult, afterEditCallback);
		} else
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
	}

	private void createNewItem(final RightClickCategoryResult result, final IMutableCardDataStore store, String newItemName, IAfterEditCallback afterEditCallback) {
		String fullUrl = result.itemUrl(newItemName);
		store.put(fullUrl, Maps.stringObjectMap(CardConstants.slingResourceType, result.collectionName), afterEditCallback);
	}

	@Override
	public void setCardUrl(String url) {
		ICallback.Utils.call(callbackToGotoUrl, url);
	}

	@Override
	public Control getControl() {
		return masterDetail.getControl();
	}

	@Override
	public Composite getComposite() {
		return masterDetail.getComposite();
	}

	public static void main(String[] args) {
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacardForCardExplorer();
		try {
			final String rootUrl = "/softwareFm/";
			final String firstUrl = "/softwareFm/data/org";

			Swts.display(Explorer.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(Composite from) throws Exception {
					final ICardDataStore cardDataStore = new CardDataStoreForRepository(from, facard);
					ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
					final CardConfig cardConfig = new BasicCardConfigurator().configure(from.getDisplay(), new CardConfig(cardFactory, cardDataStore));
					IExplorer explorer = new Explorer(from, SWT.NULL, cardConfig, rootUrl);
					explorer.setCardUrl(firstUrl);
					return explorer.getComposite();
				}
			});
		} finally {
			facard.shutdown();
		}
	}
}
