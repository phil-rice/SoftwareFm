package org.softwareFm.card.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.json.simple.JSONValue;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.collections.Sets;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

public class CardExplorerOriginal implements IHasComposite {

	class CardExplorerComposite extends SashForm {
		final CardHolder left;
		final SashForm right;
		final ScrolledComposite detail;
		final ScrolledComposite comments;
		private Listener detailResizeListener;
		private final String initialUrl;

		private final List<IUrlChangedListener> listeners = new CopyOnWriteArrayList<IUrlChangedListener>();
		Future<ICard> cardFuture;

		public CardExplorerComposite(final Composite parent, final CardConfig cardConfig, final String rootUrl, String initialUrl) {
			super(parent, SWT.HORIZONTAL);
			this.initialUrl = rootUrl;
			String loadingText = IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "card.holder.loading.text");
			left = new CardHolder(this, loadingText, loadingText, cardConfig, rootUrl, new ICallback<String>() {
				@Override
				public void process(String t) throws Exception {
					selectUrl(cardConfig, t);
				}
			});
			right = new SashForm(this, SWT.VERTICAL);
			right.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			detail = new ScrolledComposite(right, SWT.H_SCROLL);
			detail.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));

			comments = new ScrolledComposite(right, SWT.H_SCROLL);
			this.setWeights(new int[] { 2, 5 });
			right.setWeights(new int[] { 1, 1 });
			selectUrl(cardConfig, initialUrl);
		}

		public void addUrlChangedListener(IUrlChangedListener listener) {
			listeners.add(listener);
		}

		private void selectUrl(final CardConfig cardConfig, final String url) {
			cardFuture = ICardFactory.Utils.makeCard(left, cardConfig.withTitleFn(new IFunction1<String, String>() {
				@Override
				public String apply(String from) throws Exception {
					return from.substring(initialUrl.length());
				}
			}), url, new ICallback<ICard>() {
				@Override
				public void process(final ICard card) throws Exception {
					if (card == null)
						return;
					Strings.setClipboard(JSONValue.toJSONString(card.rawData()));
					new HistoryGestureListener<String>(card.getControl(), 40, 10, new History<String>(), new ICallback<String>() {
						@Override
						public void process(String t) throws Exception {
							selectUrl(cardConfig, t);
						}
					});
					Swts.resizeMeToParentsSize(card.getControl());
					card.addLineSelectedListener(new ILineSelectedListener() {
						@Override
						public void selected(KeyValue keyValue) {
							showDetailsCard(card, keyValue);
						}

					});
					for (KeyValue keyValue : card.data())
						if (keyValue.key.equals("nt:unstructured"))
							showDetailsCard(card, keyValue);
					left.setCard(card);
					for (IUrlChangedListener listener : listeners)
						listener.urlChanged(url);
				}
			});

		}

		private void showDetailsCard(final ICard parentCard, KeyValue keyValue) {
			showDetailsFor(parentCard, parentCard.cardConfig().withStyleAndSelection(SWT.FULL_SELECTION | SWT.NO_SCROLL, false).withTitleFn(CardConfig.defaultCardTitleFn), keyValue);
		}

		private void showDetailsFor(ICard parentCard, final CardConfig childCardConfig, KeyValue keyValue) {
			Swts.removeOldResizeListener(detail, detailResizeListener);
			Swts.removeAllChildren(detail);
			detail.setContent(null);
			final Composite madeDetail = makeDetail(detail, parentCard, childCardConfig, keyValue, new ICardSelectedListener() {

				@Override
				public void cardSelected(ICard card) {
					String cardUrl = card.url();
					selectUrl(childCardConfig, cardUrl);

				}
			});
			Rectangle clientArea = detail.getClientArea();
			Point size = madeDetail.computeSize(SWT.DEFAULT, clientArea.height);
			madeDetail.setSize(size);
			detail.setContent(madeDetail);

			detailResizeListener = new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (detail.isDisposed()) {
						System.out.println("Detail disposed");
					}
					if (madeDetail.isDisposed())
						System.out.println("madeDetail disposed");
					Rectangle clientArea = detail.getClientArea();
					Point size = madeDetail.computeSize(SWT.DEFAULT, clientArea.height);
					madeDetail.setSize(size);
				}
			};
			detail.addListener(SWT.Resize, detailResizeListener);

		}

		public Composite makeDetail(Composite parentComposite, final ICard parentCard, final CardConfig cardConfig, KeyValue keyValue, final ICardSelectedListener listener) {
			String key = keyValue.key;
			Object value = keyValue.value;
			if (value instanceof String) {
				Composite composite = new Composite(parentComposite, SWT.NULL);
				Swts.addGrabHorizontalAndFillGridDataToAllChildren(composite);
				return composite;

			} else if (value instanceof List)
				return makeDetailsForList(parentComposite, parentCard, cardConfig, listener, keyValue);
			else if (value instanceof Map) {
				return makeDetail(parentComposite, parentCard, cardConfig, new KeyValue(key, Arrays.asList(keyValue)), listener);
			} else
				return makeDetail(parentComposite, parentCard, cardConfig, new KeyValue(key, Strings.nullSafeToString(keyValue.value)), listener);
		}

		@SuppressWarnings("unchecked")
		private Composite makeDetailsForList(final Composite parentComposite, final ICard parentCard, final CardConfig cardConfig, final ICardSelectedListener listener, final KeyValue keyValue) {
			List<Object> list = (List<Object>) keyValue.value;
			final Composite composite = new Composite(parentComposite, SWT.NULL);
			composite.setLayout(new FillWithAspectRatioLayoutManager(5, 3));
			List<KeyValue> keyValues = Lists.newList();
			for (Object object : list)
				if (object instanceof KeyValue)
					keyValues.add((KeyValue) object);
			Collections.sort(keyValues, cardConfig.comparator);
			int i = 0;
			for (final KeyValue childKeyValue : keyValues) {
				String loadingText = IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "detail.factory.loading.text");
				final CardHolder holder = new CardHolder(composite, loadingText, childKeyValue.key + "/" + i++, cardConfig, loadingText, null);
				holder.getControl().addPaintListener(new PaintListener() {
					@Override
					public void paintControl(PaintEvent e) {
						holder.getControl().removePaintListener(this);
						populateChildBecauseNowVisible(parentCard, cardConfig, holder, listener, keyValue, childKeyValue);
					}
				});

			}
			composite.layout();
			return composite;
		}
	}

	void populateChildBecauseNowVisible(final ICard parentCard, final CardConfig cardConfig, final CardHolder holder, final ICardSelectedListener listener, final KeyValue keyValue, final KeyValue childKeyValue) {
		final Set<String> listNames = Sets.makeSet("artifact", "version", "usedby", "dependancy");
		ICardFactory.Utils.makeCard(holder, cardConfig.withStyleAndSelection(SWT.NO_SCROLL | SWT.FULL_SELECTION, false), getUrl(parentCard, childKeyValue), new ICallback<ICard>() {
			@Override
			public void process(final ICard card) throws Exception {
				if (card == null)
					return;
				if (listNames.contains(childKeyValue.key)) {
					for (KeyValue cardKv : card.data())
						if (cardKv.key.equals("nt:unstructured"))
							content.showDetailsFor(card, cardConfig, cardKv);
				} else {
//					card.getControl().addMouseListener(new MouseAdapter() {
//						@Override
//						public void mouseDown(MouseEvent e) {
//							listener.cardSelectedDown(card);
//						}
//
//						@Override
//						public void mouseUp(MouseEvent e) {
//							listener.cardSelectedUp(card, e);
//						}
//					});
					holder.setCard(card);
				}
			}
		});
	}

	private String getUrl(final ICard parentCard, final KeyValue childKeyValue) {
		return parentCard.url() + "/" + childKeyValue.key;
	}

	private final CardExplorerComposite content;

	public CardExplorerOriginal(Composite parent, CardConfig cardConfig, String rootUrl, String initialUrl) {
		content = new CardExplorerComposite(parent, cardConfig, rootUrl, initialUrl);
	}

	@Override
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public static void main(String[] args) {
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacardForCardExplorer();
		final String rootUrl = "/softwareFm/content";
		final String firstUrl = "/softwareFm/content/org";
		try {
			Swts.display(CardExplorerOriginal.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(final Composite from) throws Exception {
					final ICardDataStore cardDataStore = new CardDataStoreForRepository(from, facard);
					ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
					final CardConfig cardConfig = new BasicCardConfigurator().configure(from.getDisplay(), new CardConfig(cardFactory, cardDataStore));
					IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "navBar.prev.title");
					final CardExplorerOriginal cardExplorer = new CardExplorerOriginal(from, cardConfig, rootUrl, firstUrl);
					Swts.resizeMeToParentsSize(cardExplorer.getControl());
					return cardExplorer.getComposite();
				}
			});
		} finally {
			facard.shutdown();
		}
	}

	public void addUrlChangedListener(IUrlChangedListener listener) {
		content.addUrlChangedListener(listener);
	}

}
