package org.softwareFm.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.card.api.CardAndCollectionDataStoreVisitorMonitored;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardChangedListener;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardHolder;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.internal.details.IDetailsFactoryCallback;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

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
			detail = new ScrolledComposite(right, SWT.H_SCROLL);
			// detail.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));

			comments = new ScrolledComposite(right, SWT.H_SCROLL);
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
					if (defaultChild.equals(key))
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
		}

		private void setDetail(final ICard card, String key, Object value) {
			removeDetailContents();
			cardConfig.detailFactory.makeDetail(detail, card, cardConfig, key, value, new IDetailsFactoryCallback() {

				@Override
				public void afterEdit() {// reload
					ICallback.Utils.call(callbackToGotoUrl, card.url());
				}

				@Override
				public void gotData(final Control control) {// /layout
					detailResizeListener = new Listener() {
						@Override
						public void handleEvent(Event event) {
							// System.out.println("Resizing: " + detail.getClientArea());
							Swts.setSizeToComputedSize(control, SWT.DEFAULT, detail.getClientArea().height);
							detail.layout();
						}
					};
					detail.setContent(null);
					Swts.setSizeToComputedSize(control, SWT.DEFAULT, detail.getClientArea().height);
					detail.setContent(control);
					detail.layout();
					detail.addListener(SWT.Resize, detailResizeListener);
				}

				@Override
				public void cardSelected(ICard card) {
					ICallback.Utils.call(callbackToGotoUrl, card.url());
				}
			});
		}

		private void removeDetailContents() {
			detail.setContent(null);
			Swts.removeAllChildren(detail);
			Swts.removeOldResizeListener(detail, detailResizeListener);
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
