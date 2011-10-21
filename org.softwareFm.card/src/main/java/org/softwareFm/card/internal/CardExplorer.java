package org.softwareFm.card.internal;

import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardAndCollectionDataStoreVisitor;
import org.softwareFm.card.api.ICardChangedListener;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardHolder;
import org.softwareFm.card.api.ICardSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;
import org.softwareFm.utilities.strings.Strings;

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
					cardCollectionsDataStore.processDataFor(cardHolder, cardConfig, url, ICardAndCollectionDataStoreVisitor.Utils.sysout());
				}
			};
			cardHolder = new CardHolder(this, "loading", "Some title", cardConfig, rootUrl, callbackToGotoUrl);
			right = new SashForm(this, SWT.VERTICAL);
			right.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			detail = new ScrolledComposite(right, SWT.H_SCROLL);
			detail.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));

			comments = new ScrolledComposite(right, SWT.H_SCROLL);
			this.setWeights(new int[] { 2, 5 });
			right.setWeights(new int[] { 1, 1 });
			cardHolder.addCardChangedListener(new ICardChangedListener() {
				@Override
				public void cardChanged(ICardHolder cardHolder, ICard card) {
					detail.setContent(null);
					Swts.removeAllChildren(detail);

					IHasControl detailChild = makeDetailChild(card);

					Swts.setSizeToComputedSize(detailChild, SWT.DEFAULT, detail.getClientArea().height);// calculate size
					detail.setContent(detailChild.getControl());// this may add scroll bars
					Swts.setSizeToComputedSize(detailChild, SWT.DEFAULT, detail.getClientArea().height); // so this resets the size if scroll bars were added
				}

			});
		}

		private IHasControl makeDetailChild(ICard card) {
			Swts.removeOldResizeListener(detail, listener);
			System.out.println("makeDetailCard: " + Strings.join(card.data(), "\n  "));
			KeyValue keyValue = selectDefaultChild(card);
			if (keyValue == null)
				return null;
			final CardCollectionHolder holder = new CardCollectionHolder(detail, card.cardConfig());
			holder.addCardSelectedListener(new ICardSelectedListener() {
				@Override
				public void cardSelected(ICard card) {
					ICallback.Utils.call(callbackToGotoUrl, card.url());
				}
			});
			final Composite composite = holder.getComposite();
			composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_GREEN));
			holder.setKeyValue(card.url(), keyValue);

			listener = new Listener() {
				@Override
				public void handleEvent(Event event) {
					System.out.println("Resizine: " + detail.getClientArea());
					Swts.setSizeToComputedSize(holder, SWT.DEFAULT, detail.getClientArea().height);
					composite.layout();
				}
			};
			detail.addListener(SWT.Resize, listener);

			return holder;
		}

		private KeyValue selectDefaultChild(ICard card) {
			for (KeyValue keyValue : card.data())
				if (keyValue.key.equals("nt:unstructured"))
					return keyValue;
			for (KeyValue keyValue : card.data())
				if (CardConfig.defaultBodgedUrlFragments.contains(keyValue.key))
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
		final String rootUrl = "/softwareFm/content";
		final String firstUrl = "/softwareFm/content/org";
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
