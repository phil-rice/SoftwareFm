package org.softwareFm.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.softwareFm.card.api.CardAndCollectionDataStoreVisitorMonitored;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardChangedListener;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardHolder;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class SingleCardExplorer implements IHasComposite {

	private final CardHolder cardHolder;
	private final ICallback<String> callbackToGotoUrl;
	private final SashForm sashForm;
	private final Text text;

	public SingleCardExplorer(Composite parent, final CardConfig cardConfig, final String rootUrl) {
		sashForm = new SashForm(parent, SWT.VERTICAL);
		callbackToGotoUrl = new ICallback<String>() {
			@Override
			public void process(String url) throws Exception {
				cardConfig.cardCollectionsDataStore.processDataFor(cardHolder, cardConfig, url, CardAndCollectionDataStoreVisitorMonitored.Utils.sysout());
			}
		};
		cardHolder = new CardHolder(sashForm, "loading", "Some title", cardConfig, rootUrl, callbackToGotoUrl);
		text = new Text(sashForm, SWT.H_SCROLL | SWT.WRAP);
		Swts.resizeMeToParentsSize(cardHolder.getControl());
		cardHolder.addCardChangedListener(new ICardChangedListener() {
			@Override
			public void cardChanged(ICardHolder cardHolder, ICard card) {
				text.setText(card.data().toString());
			}

			@Override
			public void valueChanged(ICard card, String key, Object newValue) {
			}
		});
	}

	protected void setUrl(String firstUrl) {
		ICallback.Utils.call(callbackToGotoUrl, firstUrl);
	}

	@Override
	public Control getControl() {
		return sashForm;
	}

	@Override
	public Composite getComposite() {
		return sashForm;
	}


	public static void main(String[] args) {
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacardForCardExplorer();
		final String rootUrl = "/softwareFm/data";
		final String firstUrl = "/softwareFm/data/org";
		try {
			Swts.display(SingleCardExplorer.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(final Composite from) throws Exception {
					final ICardDataStore cardDataStore = new CardDataStoreForRepository(from, facard);
					ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
					final CardConfig cardConfig = new BasicCardConfigurator().configure(from.getDisplay(), new CardConfig(cardFactory, cardDataStore));
					IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "navBar.prev.title");
					final SingleCardExplorer cardExplorer = new SingleCardExplorer(from, cardConfig, rootUrl);
					cardExplorer.setUrl(firstUrl);

					return cardExplorer.getComposite();
				}
			});
		} finally {
			facard.shutdown();
		}
	}

}
