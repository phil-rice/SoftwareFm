package org.softwareFm.card.internal;

import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.future.GatedMockFuture;

public class CardHolder implements IHasComposite {

	static class CardHolderComposite extends Composite {

		private final static int topMargin = 20;
		private final static int bottomMargin = 5;
		private final static int leftMargin = 5;
		private final static int rightMargin = 5;
		private final int iconToText = 5;

		private final StackLayout layout;
		private String title;
		private Image icon;

		public CardHolderComposite(Composite parent, int style, String loadingText) {
			super(parent, style);
			setText(loadingText);
			layout = new StackLayout();
			setLayout(layout);
			addPaintListener(new PaintListener() {

				@Override
				public void paintControl(PaintEvent e) {
					int x = 0;
					if (icon != null){
						e.gc.drawImage(icon, 0, 0);
						x+= icon.getImageData().width + iconToText;
					}
					e.gc.drawText(title, x, 0);
				}
			});
		}

		@Override
		public Rectangle getClientArea() {
			Rectangle clientArea = super.getClientArea();
			int width = Math.max(clientArea.width - leftMargin - rightMargin, 0);
			int height = Math.max(clientArea.height - topMargin - bottomMargin, 0);
			return new Rectangle(clientArea.x + leftMargin, clientArea.y + topMargin, width, height);
		}

		public void setText(String title) {
			this.title = title;
		}

		public void setIcon(Image icon) {
			this.icon = icon;
		}

	}

	CardHolderComposite content;

	/** for once the style matters: set the scrolling options here */
	public CardHolder(Composite parent, String loadingText, String title) {
		content = new CardHolderComposite(parent, SWT.BORDER, loadingText);
	}

	public void setCard(final ICard card) {
		content.layout.topControl = card.getControl();
		content.layout();
		String title = Functions.call(card.cardConfig().cardTitleFn, card.url());
		content.setText(title);
		content.setIcon(Functions.call(card.cardConfig().cardIconFn, card.rawData()));
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
				final CardHolder cardHolder = new CardHolder(from, "Loading", "title");
				final Future<ICard> future = ICardFactory.Utils.makeCard(cardHolder.getComposite(), cardConfig, CardDataStoreFixture.url, new ICallback<ICard>() {
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
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							throw WrappedException.wrap(e);
						}
						((GatedMockFuture<ICard>) future).kick();
					};
				}.start();
				return cardHolder.getComposite();
			}
		});
	}

}
