package org.softwareFm.card.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardSelectedAdapter;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.card.navigation.NavBar;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.resources.IResourceGetter;

public class CardExplorer implements IHasComposite {

	class CardExplorerComposite extends SashForm {
		final CardHolder left;
		final SashForm right;
		final ScrolledComposite detail;
		final ScrolledComposite comments;
		private Listener detailResizeListener;
		private final String initialUrl;

		private final List<IUrlChangedListener> listeners = new CopyOnWriteArrayList<IUrlChangedListener>();

		public CardExplorerComposite(final Composite parent, final CardConfig cardConfig, final String rootUrl, String initialUrl) {
			super(parent, SWT.HORIZONTAL);
			this.initialUrl = rootUrl;
			String loadingText = IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "card.holder.loading.text");
			left = new CardHolder(this, loadingText, "");
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
			ICardFactory.Utils.makeCard(left.getComposite(), cardConfig.withTitleFn(new IFunction1<String, String>() {
				@Override
				public String apply(String from) throws Exception {
					return from.substring(initialUrl.length());
				}
			}), url, new ICallback<ICard>() {
				@Override
				public void process(final ICard card) throws Exception {
					if (card == null)
						return;
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
							showDetailsCard(card, url, keyValue);
						}

					});
					for (KeyValue keyValue : card.data())
						if (keyValue.key.equals("nt:unstructured"))
							showDetailsCard(card, url, keyValue);
					left.setCard(card);
					for (IUrlChangedListener listener : listeners)
						listener.urlChanged(url);
				}
			});

		}

		private void showDetailsCard(final ICard parentCard, final String url, KeyValue keyValue) {
			showDetailsFor(parentCard, parentCard.cardConfig().withStyleAndSelection(SWT.FULL_SELECTION | SWT.NO_SCROLL, false).withTitleFn(CardConfig.defaultCardTitleFn), keyValue);
		}

		private void showDetailsFor(ICard parentCard, final CardConfig childCardConfig, KeyValue keyValue) {
			Swts.removeOldResizeListener(detail, detailResizeListener);
			Swts.removeAllChildren(detail);
			detail.setContent(null);
			final Composite madeDetail = childCardConfig.detailFactory.makeDetail(detail, parentCard, childCardConfig, keyValue, new CardSelectedAdapter() {
				@Override
				public void cardSelectedUp(ICard card, MouseEvent e) {
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
					Rectangle clientArea = detail.getClientArea();
					Point size = madeDetail.computeSize(SWT.DEFAULT, clientArea.height);
					madeDetail.setSize(size);
				}
			};
			detail.addListener(SWT.Resize, detailResizeListener);
			detail.layout();
			detail.getParent().layout();
			detail.getParent().redraw();
		}
	}

	private final CardExplorerComposite content;

	public CardExplorer(Composite parent, CardConfig cardConfig, String rootUrl, String initialUrl) {
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
		final IRepositoryFacard facard = IRepositoryFacard.Utils.defaultFacard();
		final String rootUrl = "/softwareFm/content";
		final String firstUrl = "/softwareFm/content";
		final int navBarHeight = 20;
		try {
			Swts.display(CardExplorer.class.getSimpleName(), new IFunction1<Composite, Composite>() {
				@Override
				public Composite apply(final Composite from) throws Exception {
					final ICardDataStore cardDataStore = new CardDataStoreForRepository(from, facard);
					ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
					final CardConfig cardConfig = new BasicCardConfigurator().configure(from.getDisplay(), new CardConfig(cardFactory, cardDataStore));
					IResourceGetter.Utils.getOrException(cardConfig.resourceGetter, "navBar.prev.title");
					final Composite composite = new Composite(from, SWT.NULL);
					final CardExplorer cardExplorer = new CardExplorer(composite, cardConfig, rootUrl, firstUrl);
					final NavBar navBar = new NavBar(composite, navBarHeight, cardConfig, rootUrl, new ICallback<String>() {
						@Override
						public void process(String t) throws Exception {
							cardExplorer.content.selectUrl(cardConfig, t);
						}
					});

					cardExplorer.addUrlChangedListener(new IUrlChangedListener() {
						@Override
						public void urlChanged(String url) {
							navBar.noteUrlHasChanged(url);
							System.out.println();
							System.out.println();
							Swts.layoutDump(from);
						}
					});

					cardExplorer.getControl().moveBelow(navBar.getControl());
					
					Listener listener = new Listener() {
						@Override
						public void handleEvent(Event event) { 
							Rectangle parentClientArea = composite.getParent().getClientArea();
							composite.setSize(parentClientArea.width, parentClientArea.height);
							Rectangle clientArea = composite.getClientArea();
							navBar.getControl().setBounds(clientArea.x, clientArea.y, clientArea.width, navBarHeight);
							Composite composite2 = navBar.getComposite();
							composite2.layout();
							cardExplorer.getControl().setBounds(clientArea.x, clientArea.y+navBarHeight, clientArea.width, clientArea.height-navBarHeight);
						}
					};
					
					Swts.setSizeFromClientArea(composite);
					composite.getParent().addListener(SWT.Resize, listener);
					return composite;
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
