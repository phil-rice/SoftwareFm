package org.softwareFm.card.internal;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
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
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.IDetailsFactoryCallback;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.IMutableCardDataStore;
import org.softwareFm.card.api.RightClickCategoryResult;
import org.softwareFm.card.api.RightClickCategoryResult.Type;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.editors.TextEditor;
import org.softwareFm.card.editors.ValueEditorLayout;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class CardExplorer implements IHasComposite {

	static class CardExplorerComposite extends SashForm {
		final SashForm right;
		final ScrolledComposite detail;
		final ScrolledComposite comments;

		private final CardHolder cardHolder;

		ICallback<String> callbackToGotoUrl;
		private Listener detailResizeListener;
		private final CardConfig cardConfig;

		public CardExplorerComposite(final Composite parent, final CardConfig cardConfig, final String rootUrl) {
			super(parent, SWT.H_SCROLL);
			this.cardConfig = cardConfig;
			callbackToGotoUrl = new ICallback<String>() {
				@Override
				public void process(String url) throws Exception {
					cardConfig.cardCollectionsDataStore.processDataFor(cardHolder, cardConfig, url, CardAndCollectionDataStoreVisitorMonitored.Utils.sysout());
				}
			};
			cardHolder = new CardHolder(this, "loading", "Some title", cardConfig, rootUrl, callbackToGotoUrl);
			right = new SashForm(this, SWT.VERTICAL);
			// right.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			detail = new ScrolledComposite(right, SWT.H_SCROLL | SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND);
			ScrollBar hbar = detail.getHorizontalBar();
			hbar.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					Swts.redrawAllChildren(detail.getContent());
				}
			});

			comments = new ScrolledComposite(right, SWT.H_SCROLL);
			comments.setBackground(new Color(getDisplay(), 235, 242, 246));
			this.setWeights(new int[] { 2, 5 });
			right.setWeights(new int[] { 1, 1 });
			cardHolder.addCardChangedListener(new ICardChangedListener() {
				@Override
				public void cardChanged(ICardHolder cardHolder, ICard card) {
					String key = findDefaultChild(card);
					setDetail(card, key, card.data().get(key));
				}

				@Override
				public void valueChanged(ICard card, String key, Object newValue) {
					String defaultChild = findDefaultChild(card);
					if (defaultChild != null && defaultChild.equals(key))
						setDetail(card, key, card.data().get(key));
				}
			});
			cardHolder.addLineSelectedListener(new ILineSelectedListener() {
				@Override
				public void selected(ICard card, String key, Object value) {
					System.out.println("Card keyvalue: " + key);
					setDetail(card, key, value);
					// setDetailCard(card, keyValue);
				}

			});
			IAddItemProcessor itemProcessor = new IAddItemProcessor() {
				@Override
				public void process(final RightClickCategoryResult result) {
					ICard card = cardHolder.getCard();
					System.out.println("In add item processor with " + result + " and card: " + card);
					removeDetailContents();
					TextEditor editor = new TextEditor(detail, cardConfig, result.url, card.cardType(),result.collectionName, "", new IDetailsFactoryCallback() {
						private final IDetailsFactoryCallback callback = this;

						@Override
						public void updateDataStore(final IMutableCardDataStore store, String url, String key, final Object value) {
							System.out.println("Updateing data store: " + value);
							final String editorResult = Strings.stringToUrlSegment(Strings.nullSafeToString(value));
							if (result.itemType == Type.ROOT_COLLECTION) {
								createNewItem(result, store, editorResult);
							} else
								store.processDataFor(result.collectionUrl(), new ICardDataStoreCallback<Void>() {
									@Override
									public Void process(String url, Map<String, Object> data) throws Exception {
										createNewItem(result, store, editorResult);
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

						private void createNewItem(final RightClickCategoryResult result, final IMutableCardDataStore store, String newItemName) {
							String fullUrl = result.itemUrl(newItemName);
							store.put(fullUrl, Maps.stringObjectMap(CardConstants.slingResourceType, result.collectionName), callback);
						}
					}, TitleSpec.noTitleSpec(getBackground()));
					editor.getComposite().setLayout(new ValueEditorLayout());
					populateDetail(editor.getControl());
				};
			};

			cardHolder.setAddItemProcessor(itemProcessor);
		}

		private void setDetail(final ICard card, String key, Object value) {
			removeDetailContents();
			cardConfig.detailFactory.makeDetail(detail, card, cardConfig, key, value, new IDetailsFactoryCallback() {

				@Override
				public void afterEdit(String url) {// reload
					ICallback.Utils.call(callbackToGotoUrl, card.url());
				}

				@Override
				public void gotData(final Control control) {// /layout
					populateDetail(control);
				}

				@Override
				public void cardSelected(ICard card) {
					ICallback.Utils.call(callbackToGotoUrl, card.url());
				}

				@Override
				public void updateDataStore(IMutableCardDataStore store, String url, String key, Object value) {
					store.put(url, Maps.stringObjectMap(key, value), this);
				}
			});
		}

		private void populateDetail(final Control control) {
			detailResizeListener = new Listener() {
				@Override
				public void handleEvent(Event event) {
					// System.out.println("Resizing: " + detail.getClientArea());
					Size.setSizeToComputedSize(control, SWT.DEFAULT, detail.getClientArea().height);
					detail.layout();
					Control content = detail.getContent();
					if (content instanceof Composite)
						((Composite) content).layout();
				}
			};
			detail.setContent(null);
			Size.setSizeToComputedSize(control, SWT.DEFAULT, detail.getClientArea().height); // needed for scroll bar calculations
			System.out.println("detail: " + detail.isDisposed() + ", control: " + control.isDisposed());
			detail.setContent(control);
			Size.setSizeToComputedSize(control, SWT.DEFAULT, detail.getClientArea().height); // needed again if the scroll bar popped in
			if (control instanceof Composite)
				if (((Composite) control).getLayout() == null)
					((Composite) control).layout();
			detail.layout();
			detail.addListener(SWT.Resize, detailResizeListener);
		}

		private void removeDetailContents() {
			detail.setContent(null);
			Swts.removeAllChildren(detail);
			Size.removeOldResizeListener(detail, detailResizeListener);
		}

		private String findDefaultChild(ICard card) {
			String result = Functions.call(card.cardConfig().defaultChildFn, card);
			return result;
		}
	}

	private final CardExplorerComposite content;

	public CardExplorer(Composite parent, CardConfig cardConfig, String rootUrl) {
		content = new CardExplorerComposite(parent, cardConfig, rootUrl);
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public void setUrl(String url) {
		ICallback.Utils.call(content.callbackToGotoUrl, url);
	}

	public void addCardSelectedListener(ICardSelectedListener listener) {
	}

	public static void main(String[] args) {
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacardForCardExplorer();
		final String rootUrl = "/softwareFm/";
		final String firstUrl = "/softwareFm/data/org";
		try {
			Show.display(CardExplorer.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(final Composite from) throws Exception {
					final ICardDataStore cardDataStore = new CardDataStoreForRepository(from, facard);
					ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
					final CardConfig cardConfig = new BasicCardConfigurator().configure(from.getDisplay(), new CardConfig(cardFactory, cardDataStore));
					IResourceGetter.Utils.getOrException(cardConfig.resourceGetterFn, null,"navBar.prev.title");
					final CardExplorer cardExplorer = new CardExplorer(from, cardConfig, rootUrl);
					cardExplorer.setUrl(firstUrl);
					Size.resizeMeToParentsSize(cardExplorer.getControl());
					return cardExplorer.getComposite();
				}
			});
		} finally {
			facard.shutdown();
		}
	}

}
