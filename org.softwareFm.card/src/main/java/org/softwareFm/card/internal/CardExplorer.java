package org.softwareFm.card.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class CardExplorer implements IHasComposite {

	class CardExplorerComposite extends SashForm {
		final SingleCardExplorer left;
		final SashForm right;
		final ScrolledComposite detail;
		final ScrolledComposite comments;

		private final List<IUrlChangedListener> listeners = new CopyOnWriteArrayList<IUrlChangedListener>();
		Future<ICard> cardFuture;

		public CardExplorerComposite(final Composite parent, final CardConfig cardConfig, final String rootUrl) {
			super(parent, SWT.HORIZONTAL);
			left = new SingleCardExplorer(this, cardConfig, rootUrl);
			right = new SashForm(this, SWT.VERTICAL);
			right.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			detail = new ScrolledComposite(right, SWT.H_SCROLL);
			detail.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_RED));

			comments = new ScrolledComposite(right, SWT.H_SCROLL);
			this.setWeights(new int[] { 2, 5 });
			right.setWeights(new int[] { 1, 1 });
		}

		public void addUrlChangedListener(IUrlChangedListener listener) {
			listeners.add(listener);
		}

	}
	private final CardExplorerComposite content;

	public CardExplorer(Composite parent, CardConfig cardConfig, String rootUrl, String initialUrl) {
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
					final CardExplorer cardExplorer = new CardExplorer(from, cardConfig, rootUrl, firstUrl);
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
