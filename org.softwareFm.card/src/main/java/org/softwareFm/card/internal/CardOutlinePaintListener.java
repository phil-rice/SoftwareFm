package org.softwareFm.card.internal;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.api.CardConfig;
import org.softwareFm.card.internal.details.TitleSpec;

public class CardOutlinePaintListener implements PaintListener {
		private final TitleSpec titleSpec;
		private final CardConfig cardConfig;
		private final Composite cardTable;

		public CardOutlinePaintListener(TitleSpec titleSpec, CardConfig cardConfig, Composite cardTable) {
			this.titleSpec = titleSpec;
			this.cardConfig = cardConfig;
			this.cardTable = cardTable;
		}

		@Override
		public void paintControl(PaintEvent e) {
			Rectangle clientArea = cardTable.getClientArea();
			e.gc.setClipping(clientArea.x, clientArea.y+cardConfig.cornerRadius, clientArea.width-cardConfig.cornerRadius, clientArea.height);
			e.gc.drawRoundRectangle(clientArea.x, clientArea.y - cardConfig.cornerRadius, clientArea.width - 1, clientArea.height - 1 + cardConfig.cornerRadius, cardConfig.cornerRadius, cardConfig.cornerRadius);

			e.gc.setClipping(clientArea.x+clientArea.width-titleSpec.rightIndent, clientArea.y, clientArea.width, clientArea.height); //way to wide...but who cares
			e.gc.drawRoundRectangle(clientArea.x, clientArea.y , clientArea.width - 1, clientArea.height - 1 , cardConfig.cornerRadius, cardConfig.cornerRadius);
			
			e.gc.setClipping((Rectangle) null);
			e.gc.drawLine(clientArea.x, clientArea.y, clientArea.x, clientArea.y+cardConfig.cornerRadius);
//				e.gc.drawLine(clientArea.x + clientArea.width - titleSpec.rightIndent, clientArea.y, clientArea.x + clientArea.width, clientArea.y);
			e.gc.setForeground(new Color(e.display, 200, 200, 200));
			e.gc.drawLine(clientArea.x+1, clientArea.y, clientArea.width-titleSpec.rightIndent-1, clientArea.y);
		}
	}