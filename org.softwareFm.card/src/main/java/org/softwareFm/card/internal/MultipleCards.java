package org.softwareFm.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.card.api.ICardFactory;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.display.swt.Swts;

public class MultipleCards implements IHasControl {

	private final MultipleCardsComposite content;
	private final ICardDataStore cardDataStore;
	private final ICardFactory cardFactory;

	static class MultipleCardsComposite extends ScrolledComposite {

		public MultipleCardsComposite(Composite parent, CardConfig cardConfig, int style) {
			super(parent, style);
			if (cardConfig.debugLayout)
				setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_CYAN));
		}

		@Override
		public Point computeSize(int wHint, int hHint) {
			int height = 0;
			int width = 0;
			for (Control control : getChildren()) {
				Point size = control.computeSize(wHint, hHint);
				width += size.x;
				height = Math.max(size.y, height);
			}
			return new Point(height, width);
		}

	}

	public MultipleCards(Composite parent, ICardDataStore cardDataStore, ICardFactory cardFactory) {
		this.cardDataStore = cardDataStore;
		this.cardFactory = cardFactory;
		content = new MultipleCardsComposite(parent, cardFactory.getCardConfig(), SWT.H_SCROLL);
	}

	protected void layoutCards() {
	}

	public void openCardAsChildOf(ICard parent, String childNodeName) {
		if (parent != null)
			Swts.removeChildrenAfter(content, parent.getControl());
		String url = parent == null ? childNodeName : parent.url() + "/" + childNodeName;
		cardFactory.makeCard(content, cardDataStore, url);
		Point size = content.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		content.setSize(size);
		layoutCards();
	}

	@Override
	public Control getControl() {
		return content;
	}

}
