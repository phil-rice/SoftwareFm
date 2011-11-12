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


	@Override
	public void paintControl(PaintEvent e) {
		Rectangle cardBounds = cardComposite.getBounds();
		Rectangle clientArea = cardComposite.getClientArea();
		notifyListeners("cardBounds", cardBounds);
		notifyListeners("clientArea", clientArea);

		drawLeftAndBottom(e, clientArea);
		drawRight(e, clientArea);

		e.gc.setClipping((Rectangle) null);
		Rectangle topLeft = new Rectangle(clientArea.x - cardConfig.cornerRadiusComp, clientArea.y - cardConfig.cornerRadiusComp, clientArea.x - cardConfig.cornerRadiusComp, clientArea.y + -cardConfig.cornerRadiusComp + cardConfig.cornerRadius);
		e.gc.drawLine(topLeft.x, topLeft.y, topLeft.width, topLeft.height);
		notifyListeners("topLeft-Line", topLeft);
		
		Rectangle secondLine = new Rectangle(clientArea.x + clientArea.width - titleSpec.rightIndent-cardConfig.cornerRadiusComp, clientArea.y-cardConfig.cornerRadiusComp, clientArea.x + clientArea.width-cardConfig.cornerRadius, clientArea.y-cardConfig.cornerRadiusComp);
		e.gc.drawLine(secondLine.x, secondLine.y, secondLine.width, secondLine.height);
		notifyListeners("second-Line", secondLine);
		e.gc.setForeground(new Color(e.display, 200, 200, 200));
		int x = clientArea.x -cardConfig.cornerRadiusComp+1;
		int y = clientArea.y-cardConfig.cornerRadiusComp;
		e.gc.drawLine(x, y, x+clientArea.width -titleSpec.rightIndent+cardConfig.cornerRadiusComp, y);
	}

	private void drawRight(PaintEvent e, Rectangle clientArea) {
		Rectangle clipRectangle = new Rectangle(clientArea.x + clientArea.width - titleSpec.rightIndent - cardConfig.cornerRadiusComp, clientArea.y - cardConfig.cornerRadiusComp, clientArea.width + 2*cardConfig.cornerRadiusComp, clientArea.height + 2*cardConfig.cornerRadiusComp + 1);
		e.gc.setClipping(clipRectangle); // way to wide...but who cares. Don't know why need +1, but without it bottom right doesnt appear
		notifyListeners("drawRight-clip", clipRectangle);

		int x = clientArea.x - cardConfig.cornerRadiusComp;
		int y = clientArea.y - cardConfig.cornerRadiusComp;
		int width = clientArea.width + cardConfig.cornerRadiusComp;
		int height = clientArea.height + 2*cardConfig.cornerRadiusComp;
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
		Rectangle clipRectangle = new Rectangle(clientArea.x - cardConfig.cornerRadiusComp, clientArea.y + cardConfig.cornerRadius - cardConfig.cornerRadiusComp,//
				clientArea.width - cardConfig.cornerRadius + cardConfig.cornerRadiusComp, clientArea.height + cardConfig.cornerRadiusComp + cardConfig.cornerRadius);
		e.gc.setClipping(clipRectangle);
		notifyListeners("drawLeftBottom-clip", clipRectangle);
		int x = clientArea.x - cardConfig.cornerRadiusComp;
		int y = clientArea.y - cardConfig.cornerRadius - cardConfig.cornerRadiusComp;
		int width = clientArea.width + 2*cardConfig.cornerRadiusComp;
		int height = clientArea.height + cardConfig.cornerRadius + 2*cardConfig.cornerRadiusComp;
		e.gc.drawRoundRectangle(x, y, width, height, cardConfig.cornerRadius, cardConfig.cornerRadius);
		notifyListeners("drawLeftBottom-round", new Rectangle(x, y, width, height));
	}

}