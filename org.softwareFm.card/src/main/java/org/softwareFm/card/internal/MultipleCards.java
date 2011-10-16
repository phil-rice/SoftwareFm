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
import org.softwareFm.utilities.functions.IFunction1;

public class MultipleCards implements IHasControl {

	private final MultipleCardsComposite content;
	private final ICardDataStore cardDataStore;
	private final ICardFactory cardFactory;

	static class MultipleCardsComposite extends ScrolledComposite {

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
			for (Control control : getChildren()) {
				Point size = control.computeSize(SWT.DEFAULT, clientArea.height);
				control.setSize(size);
				if (control instanceof Composite)
					((Composite) control).layout();
				control.setLocation(x, 0);
				x += size.x;
			}
		}

		@Override
		public Point computeSize(int wHint, int hHint) {
			if (hHint == SWT.DEFAULT) {
				int idealHeight = idealHeight();
				int idealWidth = calculateWidth(idealHeight);
				return new Point(idealWidth, idealHeight);
			} else {
				int width = hHint * getChildren().length * cardConfig.cardWidthWeight / cardConfig.cardHeightWeigth;
				return new Point(width, hHint);
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
			for (Control control : getChildren()) {
				Point size = control.computeSize(SWT.DEFAULT, height);
				width += size.x;
			}
			return width;
		}
	}

	public MultipleCards(Composite parent, ICardDataStore cardDataStore, ICardFactory cardFactory) {
		this.cardDataStore = cardDataStore;
		this.cardFactory = cardFactory;
		content = new MultipleCardsComposite(parent, cardFactory.getCardConfig(), SWT.H_SCROLL);
	}

	protected void layoutCards() {
	}

	public ICard openCardAsChildOf(ICard parent, String childNodeName) {
		if (parent != null)
			Swts.removeChildrenAfter(content, parent.getControl());
		String url = parent == null ? childNodeName : parent.url() + "/" + childNodeName;
		ICard result = cardFactory.makeCard(content, cardDataStore, url);
		Point size = content.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		content.setSize(size);
		layoutCards();
		return result;
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
				MultipleCards multipleCards = new MultipleCards(from, cardDataStore, cardFactory);
				multipleCards.openCardAsChildOf(null, CardDataStoreFixture.url);
				multipleCards.openCardAsChildOf(null, CardDataStoreFixture.url1a);
				final Composite control = (Composite) multipleCards.getControl();
				control.getParent().addListener(SWT.Resize, new Listener() {
					@Override
					public void handleEvent(Event event) {
						Rectangle clientArea = from.getClientArea();
						control.setSize(clientArea.width, clientArea.height);
						control.layout();
					}
				});
				return (Composite) multipleCards.getControl();
			}
		});
	}

}
