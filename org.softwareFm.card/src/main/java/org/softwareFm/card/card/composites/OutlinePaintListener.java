package org.softwareFm.card.card.composites;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.softwareFm.card.configuration.CardConfig;

public class OutlinePaintListener implements PaintListener {
	private final CardConfig cardConfig;

	public OutlinePaintListener(CardConfig cardConfig) {
		this.cardConfig = cardConfig;
	}

	@Override
	public void paintControl(PaintEvent e) {
		Widget widget = e.widget;
		if (widget instanceof Composite) {
			Rectangle ca = ((Composite) widget).getClientArea();
			e.gc.drawRoundRectangle(ca.x - cardConfig.cornerRadiusComp, //
					ca.y - cardConfig.cornerRadiusComp, //
					ca.width + 2 * cardConfig.cornerRadiusComp, //
					ca.height + 2 * cardConfig.cornerRadiusComp,//
					cardConfig.cornerRadius, cardConfig.cornerRadius);
		}
	}
}