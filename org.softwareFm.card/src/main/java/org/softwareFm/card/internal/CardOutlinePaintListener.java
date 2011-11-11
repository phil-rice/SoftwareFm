package org.softwareFm.card.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.title.TitleSpec;

public class CardOutlinePaintListener implements PaintListener {
	private final TitleSpec titleSpec;
	private final CardConfig cardConfig;
	private final Composite cardComposite;

	private final List<IStringAndRectangleListener> listeners = new CopyOnWriteArrayList<IStringAndRectangleListener>();

	public CardOutlinePaintListener(TitleSpec titleSpec, CardConfig cardConfig, Composite cardComposite) {
		this.titleSpec = titleSpec;
		this.cardConfig = cardConfig;
		this.cardComposite = cardComposite;
	}

	int pullback = 2;
	int twicePullback = pullback * 2;

	@Override
	public void paintControl(PaintEvent e) {
		Rectangle cardBounds = cardComposite.getBounds();
		Rectangle clientArea = cardComposite.getClientArea();
		notifyListeners("cardBounds", cardBounds);
		notifyListeners("clientArea", clientArea);

		drawLeftAndBottom(e, clientArea);
		drawRight(e, clientArea);

		e.gc.setClipping((Rectangle) null);
		Rectangle topLeft = new Rectangle(clientArea.x - pullback, clientArea.y - pullback, clientArea.x - pullback, clientArea.y + -pullback + cardConfig.cornerRadius);
		e.gc.drawLine(topLeft.x, topLeft.y, topLeft.width, topLeft.height);
		notifyListeners("topLeft-Line", topLeft);
		
		Rectangle secondLine = new Rectangle(clientArea.x + clientArea.width - titleSpec.rightIndent-pullback, clientArea.y-pullback, clientArea.x + clientArea.width-cardConfig.cornerRadius, clientArea.y-pullback);
		e.gc.drawLine(secondLine.x, secondLine.y, secondLine.width, secondLine.height);
		notifyListeners("second-Line", secondLine);
		e.gc.setForeground(new Color(e.display, 200, 200, 200));
		e.gc.drawLine(clientArea.x + 1, clientArea.y, clientArea.width - titleSpec.rightIndent - 1, clientArea.y);
	}

	private void drawRight(PaintEvent e, Rectangle clientArea) {
		Rectangle clipRectangle = new Rectangle(clientArea.x + clientArea.width - titleSpec.rightIndent - pullback, clientArea.y - pullback, clientArea.width + twicePullback, clientArea.height + twicePullback + 1);
		e.gc.setClipping(clipRectangle); // way to wide...but who cares. Don't know why need +1, but without it bottom right doesnt appear
		notifyListeners("drawRight-clip", clipRectangle);

		int x = clientArea.x - pullback;
		int y = clientArea.y - pullback;
		int width = clientArea.width + pullback;
		int height = clientArea.height + twicePullback;
		e.gc.drawRoundRectangle(x, y, width, height, cardConfig.cornerRadius, cardConfig.cornerRadius);
		notifyListeners("drawRight-round", new Rectangle(x, y, width, height));
	}

	private void notifyListeners(String message, Rectangle rectangle) {
		for (IStringAndRectangleListener listener : listeners)
			listener.drawing(message, rectangle);
	}

	public void addListener(IStringAndRectangleListener listener) {
		listeners.add(listener);
	}

	private void drawLeftAndBottom(PaintEvent e, Rectangle clientArea) {
		Rectangle clipRectangle = new Rectangle(clientArea.x - pullback, clientArea.y + cardConfig.cornerRadius - pullback,//
				clientArea.width - cardConfig.cornerRadius + pullback, clientArea.height + pullback + cardConfig.cornerRadius);
		e.gc.setClipping(clipRectangle);
		notifyListeners("drawLeftBottom-clip", clipRectangle);
		int x = clientArea.x - pullback;
		int y = clientArea.y - cardConfig.cornerRadius - pullback;
		int width = clientArea.width + twicePullback;
		int height = clientArea.height + cardConfig.cornerRadius + twicePullback;
		e.gc.drawRoundRectangle(x, y, width, height, cardConfig.cornerRadius, cardConfig.cornerRadius);
		notifyListeners("drawLeftBottom-round", new Rectangle(x, y, width, height));
	}

}