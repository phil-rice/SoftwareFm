package org.softwareFm.card.card.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.softwareFm.card.card.CardConfig;
import org.softwareFm.card.card.ICard;
import org.softwareFm.card.card.ICardChangedListener;
import org.softwareFm.card.card.ICardFactory;
import org.softwareFm.card.card.ICardHolder;
import org.softwareFm.card.card.ILineSelectedListener;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.card.navigation.ITitleBarForCard;
import org.softwareFm.card.navigation.internal.NavBar;
import org.softwareFm.card.navigation.internal.NavTitle;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts.Show;
import org.softwareFm.display.swt.Swts.Size;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.future.GatedMockFuture;

public class CardHolder implements ICardHolder {

	public static class CardHolderLayout extends Layout {

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			CardHolderComposite cardHolder = (CardHolderComposite) composite;
			ICard card = cardHolder.card;
			CardConfig cardConfig = cardHolder.getCardConfig();
			int cardHeightHint = hHint == SWT.DEFAULT ? hHint : hHint - cardConfig.titleHeight;
			Point titleSize = cardHolder.title.getControl().computeSize(wHint, cardConfig.titleHeight);
			Point cardSize = card == null ? new Point(0, 0) : card.getControl().computeSize(wHint, cardHeightHint);
			return new Point(Math.max(cardSize.x, titleSize.x) + cardConfig.leftMargin + cardConfig.rightMargin, cardSize.y + cardConfig.topMargin + cardConfig.bottomMargin + cardConfig.titleHeight);
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			CardHolderComposite cardHolder = (CardHolderComposite) composite;
			Rectangle clientArea = cardHolder.getClientArea();
			CardConfig cardConfig = cardHolder.getCardConfig();
			int titleHeight = cardConfig.titleHeight;
			if (cardHolder.card != null) {
				Rectangle cardBounds = new Rectangle(clientArea.x, clientArea.y + titleHeight + cardConfig.topMargin, clientArea.width, clientArea.height - titleHeight - cardConfig.topMargin);
				cardHolder.card.getControl().setBounds(cardBounds);
				cardHolder.card.getComposite().layout();
			}

			cardHolder.title.getControl().setBounds(clientArea.x, clientArea.y, clientArea.width, titleHeight + cardConfig.topMargin);
			if (cardHolder.title instanceof IHasComposite)
				((IHasComposite) cardHolder.title).getComposite().layout();
		}

	}

	static class CardHolderComposite extends Composite {

		final ITitleBarForCard title;
		ICard card;
		private final CardConfig navBarCardConfig;
		private final List<ILineSelectedListener> lineListeners = new CopyOnWriteArrayList<ILineSelectedListener>();

		public CardHolderComposite(Composite parent, CardConfig navBarCardConfig, String rootUrl, ICallback<String> callbackToGotoUrl) {
			super(parent, SWT.NULL);
			this.navBarCardConfig = navBarCardConfig;
			if (navBarCardConfig == null)
				throw new NullPointerException();
			if (callbackToGotoUrl == null) {
				String titleText = Functions.call(navBarCardConfig.cardTitleFn, rootUrl);
				title = new NavTitle(this, navBarCardConfig, TitleSpec.noTitleSpec(parent.getBackground()), titleText, rootUrl);
			} else {
				NavBar bar = new NavBar(this, navBarCardConfig, rootUrl, callbackToGotoUrl);
				bar.getComposite().setLayout(new NavBar.NavBarLayout());
				title = bar;
			}
		}

		private CardConfig getCardConfig() {
			CardConfig cardConfig = card == null ? navBarCardConfig : card.cardConfig();
			return cardConfig;
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
				card.getComposite().setLayout(new Card.CardLayout());
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

//	@Override
//	public void makeAndSetTableMenu(final ICard card) {
//		Menu menu = new Menu(table);
//		item1 = new MenuItem(menu, SWT.NULL);
//
//		card.addMenuDetectListener( new Listener() {
//			@Override
//			public void handleEvent(Event event) {
//				Point location = new Point(event.x, event.y);
//				Point inMySpace = table.toControl(location);
//				TableItem item = table.getItem(inMySpace);
//				String key = (String) (item == null ? null : item.getData());
//				RightClickCategoryResult categorisation = card.cardConfig().rightClickCategoriser.categorise(card.url(), card.data(), key);
//				item1.setText("Add " + Strings.nullSafeToString(categorisation.collectionName));
//				item1.setData(categorisation);
//				item1.setEnabled(categorisation.isCollection());
//			}
//		});
//		item1.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				addItemProcessor.process((RightClickCategoryResult) item1.getData());
//			}
//		});
////		table.setMenu(menu);
//	}

	private final List<ICardChangedListener> cardListeners = new CopyOnWriteArrayList<ICardChangedListener>();
	final CardHolderComposite content;

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
	public Control getControl() {
		return content;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	public static void main(String[] args) {
		Show.display(CardHolder.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				final CardConfig cardConfig = CardDataStoreFixture.asyncCardConfig(from.getDisplay());
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
				Size.resizeMeToParentsSize(cardHolder.getControl());
				return cardHolder.getComposite();
			}
		});
	}

}
