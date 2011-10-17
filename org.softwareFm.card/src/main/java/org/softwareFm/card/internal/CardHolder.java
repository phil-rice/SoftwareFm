package org.softwareFm.card.internal;

import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.card.api.CardDataStoreFixture;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.display.composites.IHasComposite;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.future.GatedMockFuture;

public class CardHolder implements IHasComposite {

	static class CardHolderComposite extends Group {

		private final StackLayout layout;

		public CardHolderComposite(Composite parent, int style, String title) {
			super(parent, style);

			setText(title);
			layout = new StackLayout();
			setLayout(layout);
		}

		@Override
		protected void checkSubclass() {
		}

	}

	CardHolderComposite content;

	/** for once the style matters: set the scrolling options here */
	public CardHolder(Composite parent,  String title) {
		content = new CardHolderComposite(parent, SWT.NULL, title);
		Label label = new Label(content, SWT.NULL);
		label.setLocation(100, 100);
		label.setText("Loading");
		label.pack();
		content.layout.topControl = label;
	}


	public void setCard(final ICard card) {
		Swts.asyncExec(card, new Runnable() {
			@Override
			public void run() {
				content.layout.topControl = card.getControl();
				content.layout();
			}
		});
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
		final ICardFactory cardFactory = ICardFactory.Utils.cardFactory(cardDataStore);
		Swts.display(CardHolder.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				final CardHolder cardHolder = new CardHolder(from, "title");
				final Future<ICard> future = cardFactory.makeCard(cardHolder.getComposite(),SWT.FULL_SELECTION, true, CardDataStoreFixture.url, new ICallback<ICard>() {
					@Override
					public void process(ICard card) throws Exception {
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
