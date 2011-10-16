package org.softwareFm.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.CardDataStoreMock;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.exceptions.WrappedException;
import org.softwareFm.utilities.functions.IFunction1;

public class MultipleCards implements IHasControl {

	private final MultipleCardsComposite content;
	private final ICardDataStore cardDataStore;
	private final ICardFactory cardFactory;

	static class MultipleCardsComposite extends Composite {

		private final CardConfig cardConfig;

		public MultipleCardsComposite(Composite parent, CardConfig cardConfig, int style) {
			super(parent, style);
			this.cardConfig = cardConfig;
			if (cardConfig.debugLayout)
				setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_CYAN));
		}

		@Override
		public void setLayout(Layout layout) {
		}

		@Override
		public void layout(boolean changed) {
			Rectangle clientArea = getClientArea();
			int x = 0;
			System.out.println("MultipleCards/layout.clientArea: "+ clientArea);
			for (Control control : getChildren()) {
				Point size = control.computeSize(SWT.DEFAULT, clientArea.height);
				System.out.println("MultipleCards/layout.size: "+ size);
				control.setSize(size);
				if (control instanceof Composite)
					((Composite) control).layout();
				control.setLocation(x, 0);
				x += size.x;
			}
		}

		@Override
		public Point computeSize(int wHint, int hHint) {
			int idealHeight = idealHeight();
			int idealWidth = calculateWidth(idealHeight);
			if (hHint == SWT.DEFAULT) {
				return new Point(idealWidth, idealHeight);
			} else {
				int width = hHint * getChildren().length * cardConfig.cardWidthWeight / cardConfig.cardHeightWeigth;
				return new Point(Math.min(width, idealWidth), Math.min(hHint, idealHeight));
			}
		}

		private int idealHeight() {
			int height = 0;
			Control[] children = getChildren();
			for (Control control : children) {
				Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				height = Math.max(size.y, height);
			}
			return height;
		}

		private int calculateWidth(int height) {
			int width = 0;
			for (Control control :  getChildren()) {
				Point size = control.computeSize(SWT.DEFAULT, height);
				width += size.x;
			}
			return width;
		}
	}

	public MultipleCards(Composite parent, ICardDataStore cardDataStore, ICardFactory cardFactory) {
		this.cardDataStore = cardDataStore;
		this.cardFactory = cardFactory;
		content = new MultipleCardsComposite(parent, cardFactory.getCardConfig(), SWT.NULL);
	}

	protected void layoutCards() {
		content.layout();
	}

	public ICard openCardAsChildOf(ICard parent, String childNodeName) {
		try {
			if (parent != null)
				Swts.removeChildrenAfter(content, parent.getControl());
			String url = parent == null ? childNodeName : parent.url() + "/" + childNodeName;
			ICard result = cardFactory.makeCard(content, cardDataStore, url);
			Swts.asyncExec(result, new Runnable() {
				@Override
				public void run() {
					// this exists because the makeCard method has an asyncexec which must have finished before we start.
					Point size = content.computeSize(SWT.DEFAULT, SWT.DEFAULT);
					content.setSize(size);
					System.out.println("Setting  content size: "+ content.getSize() );
					content.layout();
				}
			});
			return result;
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public Control getControl() {
		return content;
	}

	public static void main(String[] args) {
		final CardDataStoreMock cardDataStore = CardDataStoreFixture.rawCardStore();
		final ICardFactory cardFactory = ICardFactory.Utils.cardFactory();
		Swts.displayNoLayout(MultipleCards.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(final Composite from) throws Exception {
				final ScrolledComposite content = new ScrolledComposite(from, SWT.H_SCROLL|SWT.BORDER);
				final MultipleCards multipleCards = new MultipleCards(content, cardDataStore, cardFactory);
				content.setContent(multipleCards.getControl());
				multipleCards.openCardAsChildOf(null, CardDataStoreFixture.url);
				multipleCards.openCardAsChildOf(null, CardDataStoreFixture.url1a);
				multipleCards.layoutCards();
				from.addListener(SWT.Resize, new Listener() {
					@Override
					public void handleEvent(Event event) {
						Rectangle clientArea = from.getClientArea();
						Point size = multipleCards.content.computeSize(SWT.DEFAULT, clientArea.height);
						multipleCards.content.setSize(size);
						content.setSize(clientArea.width, clientArea.height);
						multipleCards.content.layout();
					}
				});
				return (Composite) multipleCards.getControl();
			}
		});
	}

}
