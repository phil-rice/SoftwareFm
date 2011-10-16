package org.softwareFm.card.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.api.ICard;
import org.softwareFm.card.api.ILine;
import org.softwareFm.card.api.ILineFactory;
import org.softwareFm.card.api.ILineSelectedListener;
import org.softwareFm.card.api.KeyValue;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.collections.Lists;

public class Card implements ICard {

	final CardComposite content;
	Future<?> future;
	private final String url;

	static class CardComposite extends Group {
		List<ILine> lines = Lists.newList();
		private final CardConfig cardConfig;
		private final Listener listener;
		private Rectangle lastParentClientArea;
		private final List<ILineSelectedListener> listeners = new CopyOnWriteArrayList<ILineSelectedListener>();
		private Point lastSize;

		public CardComposite(Composite parent, CardConfig cardConfig, String url, String title) {
			super(parent, SWT.NULL);
			this.cardConfig = cardConfig;

			setText(title);
			if (cardConfig.debugLayout)
				setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			lines.add(new TextLine(this, cardConfig, "loading"));
			listener = new Listener() {
				@Override
				public void handleEvent(Event event) {
					layoutOutCard();
				}
			};
			getParent().addListener(SWT.Resize, listener);
			layoutOutCard();
		}

		public void layoutOutCard() {
			Point size = computeSize(SWT.DEFAULT,SWT.DEFAULT);
			setSize(size);
			Rectangle clientArea = getClientArea();

			int lineWidth = clientArea.width - cardConfig.lineMarginX * 2;
			int titleWidth = lineWidth * cardConfig.nameWeight / (cardConfig.valueWeight + cardConfig.nameWeight);
			int y = clientArea.y + cardConfig.lineMarginY;

			if (cardConfig.debugLayout)
				System.out.println("raw: " + clientArea + " actual: " + clientArea.width + ", " + size.y + " line: " + titleWidth);
			for (ILine line : lines) {
				line.getControl().setVisible(y + cardConfig.lineHeight < size.y);
				line.setWidth(lineWidth, titleWidth);
				line.getControl().setLocation(clientArea.x + cardConfig.lineMarginX, y);
				y += cardConfig.lineHeight + cardConfig.lineToLineGap;
			}
		}

		@Override
		public Point computeSize(int wHint, int hHint, boolean changed) {
			Rectangle parentClientArea = getParent().getClientArea();
			if (parentClientArea.equals(lastParentClientArea))
				return lastSize;
			this.lastParentClientArea = parentClientArea;
			int maxWidth = cardConfig.cardHeightWeigth * parentClientArea.height / cardConfig.cardWidthWeight;
			int maxHeight = cardConfig.cardWidthWeight * parentClientArea.width / cardConfig.cardHeightWeigth;

			int width = Math.min(parentClientArea.width, maxWidth);
			int height = Math.min(parentClientArea.height, maxHeight);
			Point size = new Point(width, height);
			lastSize = size;
			return size;
		}

		@Override
		public void dispose() {
			getParent().removeListener(SWT.Resize, listener);
			super.dispose();
		}

		public void populate(ILineFactory lineFactory, List<KeyValue> list) {
			lastParentClientArea = null;
			Swts.removeAllChildren(this);
			lines.clear();
			for (final KeyValue keyValue : list) {
				final ILine line = lineFactory.make(this, keyValue);
				line.addSelectedListener(new Listener() {
					@Override
					public void handleEvent(Event event) {
						for (ILineSelectedListener listener : listeners)
							listener.selected(keyValue, line);
					}
				});
				lines.add(line);

			}
			layoutOutCard();
		}

		@Override
		protected void checkSubclass() {
		}

	}

	public Card(Composite parent, final CardConfig cardConfig, String url, String title) { // order matters in the map, so it's likely to be a linked map
		content = new CardComposite(parent, cardConfig, url, title);
		this.url = url;
	}

	@Override
	public Control getControl() {
		return content;
	}

	public void setFuture(Future<?> future) {
		this.future = future;

	}

	@Override
	public void addLineSelectedListener(ILineSelectedListener listener) {
		content.listeners.add(listener);
	}

	public void populate(ILineFactory lineFactory, List<KeyValue> list) {
		content.populate(lineFactory, list);
	}

	@Override
	public String url() {
		return url;
	}


}
