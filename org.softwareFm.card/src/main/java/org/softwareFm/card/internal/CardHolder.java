package org.softwareFm.card.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.IAddItemProcessor;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardChangedListener;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.api.ICardHolder;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.RightClickCategoryResult;
import org.softwareFm.card.internal.details.TitleSpec;
import org.softwareFm.card.navigation.ITitleBarForCard;
import org.softwareFm.card.navigation.NavBar;
import org.softwareFm.card.navigation.NavTitle;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.future.GatedMockFuture;
import org.softwareFm.utilities.strings.Strings;

public class CardHolder implements ICardHolder {

	static class CardHolderComposite extends Composite {

		final ITitleBarForCard title;
		ICard card;
		private final CardConfig navBarCardConfig;
		private final List<ILineSelectedListener> lineListeners = new CopyOnWriteArrayList<ILineSelectedListener>();
		private final ICallback<String> callbackToGotoUrl;

		public CardHolderComposite(Composite parent, CardConfig navBarCardConfig, String rootUrl, ICallback<String> callbackToGotoUrl) {
			super(parent, SWT.NULL);
			this.navBarCardConfig = navBarCardConfig;
			this.callbackToGotoUrl = callbackToGotoUrl;
			if (navBarCardConfig == null)
				throw new NullPointerException();
			if (callbackToGotoUrl == null){
				String titleText = Functions.call(navBarCardConfig.cardTitleFn, rootUrl);
				title = new NavTitle(this, navBarCardConfig, TitleSpec.noTitleSpec(parent.getBackground()), titleText, rootUrl);
			}
			else
				title = new NavBar(this, navBarCardConfig, rootUrl, callbackToGotoUrl);
			addListener(SWT.Resize, new Listener() {
				@Override
				public void handleEvent(Event event) {
					layout();
				}
			});
		}

		@Override
		public Rectangle getClientArea() {
			CardConfig cardConfig = getCardConfig();
			Rectangle clientArea = super.getClientArea();
			int cardWidth = clientArea.width - cardConfig.leftMargin - cardConfig.rightMargin;
			int cardHeight = clientArea.height - cardConfig.topMargin - cardConfig.bottomMargin;
			Rectangle result = new Rectangle(clientArea.x + cardConfig.leftMargin, clientArea.y + cardConfig.topMargin, cardWidth, cardHeight);
			return result;
		}

		private CardConfig getCardConfig() {
			CardConfig cardConfig = card == null ? navBarCardConfig : card.cardConfig();
			return cardConfig;
		}

		@Override
		public void setLayout(Layout layout) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void layout(boolean changed) {
			Rectangle clientArea = getClientArea();
			int titleHeight = getCardConfig().titleHeight;
			if (card != null) {
				card.getControl().setBounds(clientArea.x, clientArea.y + titleHeight, clientArea.width, clientArea.height - titleHeight);
				card.getComposite().layout();
			}
			title.getControl().setBounds(clientArea.x, clientArea.y, clientArea.width, titleHeight);
			if (title instanceof IHasComposite)
				((IHasComposite) title).getComposite().layout();
		}

		@Override
		public Point computeSize(int wHint, int hHint) {
			return super.computeSize(wHint, hHint);
		}

		public void setCard(ICard card) {
			if (this.card == card)
				return;
			if (this.card != null)
				this.card.getComposite().dispose();
			try {
				if (card.cardConfig() == null)
					throw new NullPointerException();
				this.card = card;
				title.setUrl(card);
				layout();

				card.addLineSelectedListener(new ILineSelectedListener() {
					@Override
					public void selected(ICard card, String key, Object value) {
						for (ILineSelectedListener listener : lineListeners)
							listener.selected(card, key, value);
					}
				});
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}
	}

	@Override
	public void makeAndSetTableMenu(final ICard card) {
		final Table table = (Table) card.getControl();
		if (table.getMenu() != null)
			table.getMenu().dispose();

		if (addItemProcessor == null)
			return;

		Menu menu = new Menu(table);
		final MenuItem item1 = new MenuItem(menu, SWT.NULL);

		table.addListener(SWT.MenuDetect, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Point location = new Point(event.x, event.y);
				Point inMySpace = table.toControl(location);
				TableItem item = table.getItem(inMySpace);
				String key = (String) (item == null ? null : item.getData());
				RightClickCategoryResult categorisation = card.cardConfig().rightClickCategoriser.categorise(card.url(), card.data(), key);
				item1.setText("Add " + Strings.nullSafeToString(categorisation.collectionName));
				item1.setData(categorisation);
				item1.setEnabled(categorisation.isCollection());
			}
		});
		item1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addItemProcessor.process((RightClickCategoryResult) item1.getData());
			}
		});
		table.setMenu(menu);
	}

	private final List<ICardChangedListener> cardListeners = new CopyOnWriteArrayList<ICardChangedListener>();
	final CardHolderComposite content;
	private IAddItemProcessor addItemProcessor;

	public CardHolder(Composite parent, String loadingText, String title, CardConfig cardConfig, String rootUrl, ICallback<String> callbackToGotoUrl) {
		content = new CardHolderComposite(parent, cardConfig, rootUrl, callbackToGotoUrl);
	}

	public ICard getCard() {
		return content.card;
	}

	@Override
	public void setCard(final ICard card) {
		content.setCard(card);
		for (ICardChangedListener listener : cardListeners) {
			listener.cardChanged(this, card);
			card.addValueChangedListener(listener);
		}

	}

	public void addCardChangedListener(ICardChangedListener listener) {
		cardListeners.add(listener);
	}

	public void addLineSelectedListener(ILineSelectedListener listener) {
		content.lineListeners.add(listener);
	}

	@Override
	public void setAddItemProcessor(IAddItemProcessor processor) {
		addItemProcessor = processor;
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
		final ICardDataStore cardDataStore = CardDataStoreFixture.rawAsyncCardStore();
		final ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
		final CardConfig cardConfig = new CardConfig(cardFactory, cardDataStore);
		Swts.display(CardHolder.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				final CardHolder cardHolder = new CardHolder(from, "Loading", "title", cardConfig, CardDataStoreFixture.url, ICallback.Utils.<String> noCallback());
				final Future<ICard> future = ICardFactory.Utils.makeCard(cardHolder, cardConfig, CardDataStoreFixture.url1a, new ICallback<ICard>() {
					@Override
					public void process(ICard card) throws Exception {
						if (card == null)
							return;
						cardHolder.setCard(card);
					}
				});
				new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							throw WrappedException.wrap(e);
						}
						((GatedMockFuture<?, ?>) future).kick();
					};
				}.start();
				Swts.resizeMeToParentsSize(cardHolder.getControl());
				return cardHolder.getComposite();
			}
		});
	}

}
