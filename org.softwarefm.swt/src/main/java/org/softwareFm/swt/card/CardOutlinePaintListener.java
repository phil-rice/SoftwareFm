/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.title.TitleSpec;

public class CardOutlinePaintListener implements PaintListener {
	private final TitleSpec titleSpec;
	private final CardConfig cardConfig;


	public CardOutlinePaintListener(TitleSpec titleSpec, CardConfig cardConfig) {
		this.titleSpec = titleSpec;
		this.cardConfig = cardConfig;
	}

	@Override
	public void paintControl(PaintEvent e) {
		Composite cardComposite = (Composite) e.widget;
		Rectangle clientArea = cardComposite.getClientArea();

		drawLeftAndBottom(e, clientArea);
		drawRight(e, clientArea);

		e.gc.setClipping((Rectangle) null);
		Rectangle topLeft = new Rectangle(clientArea.x - cardConfig.cornerRadiusComp, clientArea.y - cardConfig.cornerRadiusComp, clientArea.x - cardConfig.cornerRadiusComp, clientArea.y + -cardConfig.cornerRadiusComp + cardConfig.cornerRadius);
		e.gc.drawLine(topLeft.x, topLeft.y, topLeft.width, topLeft.height);

		Rectangle secondLine = new Rectangle(clientArea.x + clientArea.width - titleSpec.rightIndent - cardConfig.cornerRadiusComp, clientArea.y - cardConfig.cornerRadiusComp, clientArea.x + clientArea.width - cardConfig.cornerRadius, clientArea.y - cardConfig.cornerRadiusComp);
		e.gc.drawLine(secondLine.x, secondLine.y, secondLine.width, secondLine.height);
		e.gc.setForeground(new Color(e.display, 200, 200, 200));
		int x = clientArea.x - cardConfig.cornerRadiusComp + 1;
		int y = clientArea.y - cardConfig.cornerRadiusComp;
		e.gc.drawLine(x, y, x + clientArea.width - titleSpec.rightIndent + cardConfig.cornerRadiusComp, y);
	}

	private void drawRight(PaintEvent e, Rectangle clientArea) {
		Rectangle clipRectangle = new Rectangle(clientArea.x + clientArea.width - titleSpec.rightIndent - cardConfig.cornerRadiusComp, clientArea.y - cardConfig.cornerRadiusComp, clientArea.width + 2 * cardConfig.cornerRadiusComp, clientArea.height + 2 * cardConfig.cornerRadiusComp + 1);
		e.gc.setClipping(clipRectangle); // way to wide...but who cares. Don't know why need +1, but without it bottom right doesnt appear

		int x = clientArea.x - cardConfig.cornerRadiusComp;
		int y = clientArea.y - cardConfig.cornerRadiusComp;
		int width = clientArea.width + cardConfig.cornerRadiusComp;
		int height = clientArea.height + 2 * cardConfig.cornerRadiusComp;
		e.gc.drawRoundRectangle(x, y, width, height, cardConfig.cornerRadius, cardConfig.cornerRadius);
	}

	private void drawLeftAndBottom(PaintEvent e, Rectangle clientArea) {
		Rectangle clipRectangle = new Rectangle(clientArea.x - cardConfig.cornerRadiusComp, clientArea.y + cardConfig.cornerRadius - cardConfig.cornerRadiusComp,//
				clientArea.width - cardConfig.cornerRadius + cardConfig.cornerRadiusComp, clientArea.height + cardConfig.cornerRadiusComp + cardConfig.cornerRadius);
		e.gc.setClipping(clipRectangle);
		int x = clientArea.x - cardConfig.cornerRadiusComp;
		int y = clientArea.y - cardConfig.cornerRadius - cardConfig.cornerRadiusComp;
		int width = clientArea.width + 2 * cardConfig.cornerRadiusComp;
		int height = clientArea.height + cardConfig.cornerRadius + 2 * cardConfig.cornerRadiusComp;
		e.gc.drawRoundRectangle(x, y, width, height, cardConfig.cornerRadius, cardConfig.cornerRadius);
	}

}