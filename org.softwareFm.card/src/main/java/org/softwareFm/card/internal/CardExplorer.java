package org.softwareFm.card.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.card.api.CardAndCollectionDataStoreVisitorMock;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardChangedListener;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardDataStoreCallback;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardHolder;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class CardExplorer implements IHasComposite {

	static class CardExplorerComposite extends SashForm {
		final SashForm right;
		final ScrolledComposite detail;
		final ScrolledComposite comments;

		Future<ICard> cardFuture;
		private final CardHolder cardHolder;
		private final CardCollectionsDataStore cardCollectionsDataStore = new CardCollectionsDataStore() {
			@Override
			protected String findFollowOnUrlFragment(KeyValue keyValue) {
				return CardConfig.defaultBodgedUrlFragments.contains(keyValue.key) ? keyValue.key : null;
			};
		};
		ICallback<String> callbackToGotoUrl;
		private Listener listener;

		public CardExplorerComposite(final Composite parent, final CardConfig cardConfig, final String rootUrl) {
			super(parent, SWT.H_SCROLL);
			callbackToGotoUrl = new ICallback<String>() {
				@Override
				public void process(String url) throws Exception {
					cardCollectionsDataStore.processDataFor(cardHolder, cardConfig, url, CardAndCollectionDataStoreVisitorMock.Utils.sysout());
				}
			};
			cardHolder = new CardHolder(this, "loading", "Some title", cardConfig, rootUrl, callbackToGotoUrl);
			right = new SashForm(this, SWT.VERTICAL);
			// right.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			detail = new ScrolledComposite(right, SWT.H_SCROLL);
			// detail.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));

			comments = new ScrolledComposite(right, SWT.H_SCROLL);
			this.setWeights(new int[] { 2, 5 });
			right.setWeights(new int[] { 1, 1 });
			cardHolder.addCardChangedListener(new ICardChangedListener() {
				@Override
				public void cardChanged(ICardHolder cardHolder, ICard card) {
					KeyValue keyValue = findDefaultChild(card);
					setDetailCard(card, keyValue);
				}

			});
			cardHolder.addLineSelectedListener(new ILineSelectedListener() {
				@Override
				public void selected(ICard card, KeyValue keyValue) {
					System.out.println("Card keyvalue: " + keyValue);
					setDetailCard(card, keyValue);
				}
			});
		}

		private void setDetailCard(final ICard card, final KeyValue keyValue) {
			rawSetDetail(card, keyValue);
			if (keyValue != null)
				if (CardConfig.defaultBodgedUrlFragments.contains(keyValue.key)) {
					final String newUrl = card.url() + "/" + keyValue.key;
					card.cardConfig().cardDataStore.processDataFor(newUrl, new ICardDataStoreCallback<Void>() {
						@SuppressWarnings("unchecked")
						@Override
						public Void process(String url, Map<String, Object> result) throws Exception {
							List<KeyValue> list = card.cardConfig().aggregator.apply(result);
							Collections.sort(list, card.cardConfig().comparator);
							for (KeyValue nextKeyValue : list)
								if (CardConfig.anotherBodge.contains(nextKeyValue.key)) {
									List<KeyValue> list2 = Lists.newList();
									for (KeyValue kv : (List<KeyValue>) nextKeyValue.value)
										list2.add(new KeyValue(keyValue.key + "/" + kv.key, kv.value));
									KeyValue keyValue2 = new KeyValue(keyValue.key, list2);
									rawSetDetail(card, keyValue2);
									return null;
								}
							return null;
						}

						@Override
						public Void noData(String url) throws Exception {
							return null;
						}
					});
				}
		}

		private void rawSetDetail(final ICard card, final KeyValue keyValue) {
			detail.setContent(null);
			Swts.removeAllChildren(detail);
			Swts.removeOldResizeListener(detail, listener);
			// System.out.println("makeDetailCard: " + Strings.join(card.data(), "\n  "));
			if (keyValue == null)
				return;

			final CardCollectionHolder detailChild = new CardCollectionHolder(detail, card.cardConfig());
			detailChild.addCardSelectedListener(new ICardSelectedListener() {
				@Override
				public void cardSelected(ICard card) {
					ICallback.Utils.call(callbackToGotoUrl, card.url());
				}
			});
			final Composite composite = detailChild.getComposite();
			composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_GREEN));
			detailChild.setKeyValue(card.url(), keyValue);

			listener = new Listener() {
				@Override
				public void handleEvent(Event event) {
					// System.out.println("Resizing: " + detail.getClientArea());
					Swts.setSizeToComputedSize(detailChild, SWT.DEFAULT, detail.getClientArea().height);
					composite.layout();
				}
			};
			detail.addListener(SWT.Resize, listener);

			if (detailChild != null) {
				Rectangle initialClientArea = detail.getClientArea();
				Swts.setSizeToComputedSize(detailChild, SWT.DEFAULT, initialClientArea.height);// calculate size

				detail.setContent(detailChild.getControl());// this may add scroll bars

				Rectangle afterPotentialScrollBarsAddedClientArea = detail.getClientArea();
				Swts.setSizeToComputedSize(detailChild, SWT.DEFAULT, afterPotentialScrollBarsAddedClientArea.height); // so this resets the size if scroll bars were added
			}
		}

		private KeyValue findDefaultChild(ICard card) {
			for (KeyValue keyValue : card.data())
				if (CardConfig.defaultBodgedUrlFragments.contains(keyValue.key)) {
					Object value = keyValue.value;
					return keyValue;
				}
			for (KeyValue keyValue : card.data())
				if (keyValue.key.equals("nt:unstructured"))
					return keyValue;
			for (KeyValue keyValue : card.data())
				if (CardConfig.anotherBodge.contains(keyValue.key))
					return keyValue;
			return null;
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

	public static void main(String[] args) {
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacardForCardExplorer();
		final String rootUrl = "/softwareFm/data";
		final String firstUrl = "/softwareFm/data/org";
		try {
			Swts.display(CardExplorer.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(final Composite from) throws Exception {
					final ICardDataStore cardDataStore = new CardDataStoreForRepository(from, facard);
					ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
					final CardConfig cardConfig = new BasicCardConfigurator().configure(from.getDisplay(), new CardConfig(cardFactory, cardDataStore));
					IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "navBar.prev.title");
					final CardExplorer cardExplorer = new CardExplorer(from, cardConfig, rootUrl);
					cardExplorer.setUrl(firstUrl);
					Swts.resizeMeToParentsSize(cardExplorer.getControl());
					return cardExplorer.getComposite();
				}
			});
		} finally {
			facard.shutdown();
		}
	}

}
