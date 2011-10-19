package org.softwareFm.card.internal;

import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.card.navigation.NavBar;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.future.GatedMockFuture;
import org.softwareFm.utilities.strings.Strings;

public class CardHolder implements IHasComposite {

	static class CardHolderComposite extends Composite {

		private final static int topMargin = 20;
		private final static int bottomMargin = 5;
		private final static int leftMargin = 5;
		private final static int rightMargin = 5;
		private final NavBar navBar;
		private ICard card;
		protected String title;

		public CardHolderComposite(Composite parent, int style, final String loadingText, CardConfig cardConfig, String rootUrl, ICallback<String> callbackToGotoUrl) {
			super(parent, style);
			if (callbackToGotoUrl == null)
				navBar = null;
			else
				navBar = new NavBar(this, topMargin, cardConfig, rootUrl, callbackToGotoUrl);
			addPaintListener(new PaintListener() {

				@Override
				public void paintControl(PaintEvent e) {
					int x = 0;
					if (navBar == null)
						if (title == null)
							e.gc.drawText(loadingText, x, 0);
						else
							e.gc.drawText(title, x, 0);
				}
			});
			addListener(SWT.Resize, new Listener() {
				@Override
				public void handleEvent(Event event) {
					layout();
				}
			});
		}

		@Override
		public Rectangle getClientArea() {
			Rectangle clientArea = super.getClientArea();
			int width = Math.max(clientArea.width - leftMargin - rightMargin, 0);
			int height = Math.max(clientArea.height - bottomMargin, 0);
			Rectangle result = new Rectangle(clientArea.x + leftMargin, clientArea.y, width, height);
			return result;
		}

		@Override
		public void setLayout(Layout layout) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void layout(boolean changed) {
			Rectangle clientArea = getClientArea();
			if (navBar == null) {
				if (card != null)
					card.getControl().setBounds(0, topMargin, clientArea.width, clientArea.height - topMargin - bottomMargin);
				return;
			}

			Control navControl = navBar.getControl();
			int navHeight = navControl.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
			navControl.setBounds(0, 0, clientArea.width, navHeight);
			if (navBar != null)
				navBar.getComposite().layout();
			if (card != null)
				card.getControl().setBounds(0, navHeight + 1, clientArea.width, clientArea.height - 1 - navHeight - bottomMargin);
		}

		@Override
		public Point computeSize(int wHint, int hHint) {
			return super.computeSize(wHint, hHint);
		}

		public void setCard(ICard card) {
			try {
				this.card = card;
				if (navBar != null)
					navBar.noteUrlHasChanged(card.url());
				layout();
				title = Strings.lastSegmentFn("/").apply(card.url());
				Swts.layoutDump(this);
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}

		}

	}

	CardHolderComposite content;

	public CardHolder(Composite parent, String loadingText, String title, CardConfig cardConfig, String rootUrl, ICallback<String> callbackToGotoUrl) {
		content = new CardHolderComposite(parent, SWT.BORDER, loadingText, cardConfig, rootUrl, callbackToGotoUrl);
	}

	public CardHolder(Composite parent, String loadingText, String rootUrl) {
		content = new CardHolderComposite(parent, SWT.BORDER, loadingText, null, rootUrl, null);
	}

	public void setCard(final ICard card) {
		content.setCard(card);
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
				final Future<ICard> future = ICardFactory.Utils.makeCard(cardHolder.getComposite(), cardConfig, CardDataStoreFixture.url1a, new ICallback<ICard>() {
					@Override
					public void process(ICard card) throws Exception {
						if (card == null)
							return;
						cardHolder.setCard(card);
						Swts.layoutDump(from);
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
						((GatedMockFuture<ICard>) future).kick();
					};
				}.start();
				Swts.resizeMeToParentsSize(cardHolder.getControl());
				return cardHolder.getComposite();
			}
		});
	}

}
