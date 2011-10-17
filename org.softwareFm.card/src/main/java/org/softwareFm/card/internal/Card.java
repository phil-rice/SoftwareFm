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
import org.eclipse.swt.widgets.Layout;
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
	private List<KeyValue> data;

	static class CardComposite extends Group {
		List<ILine> lines = Lists.newList();
		private final CardConfig cardConfig;
		private final List<ILineSelectedListener> listeners = new CopyOnWriteArrayList<ILineSelectedListener>();

		public CardComposite(Composite parent, CardConfig cardConfig, String url, String title) {
			super(parent, SWT.NULL);
			this.cardConfig = cardConfig;

			setText(title);
			if (cardConfig.debugLayout)
				setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			lines.add(new TextLine(this, cardConfig, "loading"));
		}

		@Override
		public void setLayout(Layout layout) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void layout(boolean changed) {
			Rectangle clientArea = getClientArea();

			int lineWidth = clientArea.width - cardConfig.lineMarginX * 2;
			int titleWidth = lineWidth * cardConfig.nameWeight / (cardConfig.valueWeight + cardConfig.nameWeight);
			int y = clientArea.y + cardConfig.lineMarginY;

			if (cardConfig.debugLayout)
				System.out.println("raw: " + clientArea + " actual: " + clientArea.width + ", " + clientArea.height + " line: " + titleWidth);
			for (ILine line : lines) {
				line.getControl().setVisible(y + cardConfig.lineHeight < clientArea.height);
				line.setWidth(lineWidth, titleWidth);
				line.getControl().setLocation(clientArea.x + cardConfig.lineMarginX, y);
				y += cardConfig.lineHeight + cardConfig.lineToLineGap;
			}
		}

		@Override
		public Point computeSize(int wHint, int hHint, boolean changed) {
			int idealHeight = getChildren().length * (cardConfig.lineHeight + cardConfig.lineToLineGap);
			int idealWidth = heightToWidth(idealHeight);
			if (wHint == SWT.DEFAULT)
				if (hHint == SWT.DEFAULT) {
					return new Point(idealWidth, idealHeight);
				} else {
					int width = heightToWidth(hHint);
					return new Point(width, hHint);
				}
			else if (hHint == SWT.DEFAULT) {
				int height = widthToHeight(wHint);
				return new Point(wHint, height);
			} else {
				int heightForwHint = widthToHeight(wHint);
				int clippedheight = Math.min(heightForwHint, hHint);
				int widthForClippedHeight = heightToWidth(clippedheight);
				int width = Math.min(wHint, widthForClippedHeight);
				int height = widthToHeight(width);
				Point result = new Point(width, height);
				return result;
			}
		}

		private int widthToHeight(int wHint) {
			return wHint * cardConfig.cardHeightWeigth / cardConfig.cardWidthWeight;
		}

		private int heightToWidth(int hHint) {
			return hHint * cardConfig.cardWidthWeight / cardConfig.cardHeightWeigth;
		}

		public void populate(ILineFactory lineFactory, List<KeyValue> list) {
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
			layout();
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
		this.data = list;
		content.populate(lineFactory, list);
	}

	@Override
	public String url() {
		return url;
	}

	@Override
	public Future<?> getPopulateFuture() {
		return future;
	}

	@Override
	public Composite getComposite() {
		return content;
	}

	@Override
	public List<KeyValue> data() {
		return data;
	}
}
