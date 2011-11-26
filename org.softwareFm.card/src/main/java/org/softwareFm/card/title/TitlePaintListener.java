/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.title;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.configuration.CardConfig;

public class TitlePaintListener implements PaintListener {
	private final CardConfig cardConfig;
	private TitleSpec titleSpec;
	private String title;
	private static int globalId;
	@SuppressWarnings("unused")
	private final int id;

	public TitlePaintListener(CardConfig cardConfig, TitleSpec titleSpec, String title) {
		this.id = globalId++;
		this.cardConfig = cardConfig;
		this.titleSpec = titleSpec;
		this.title = title;
	}

	@Override
	public void paintControl(PaintEvent e) {
		Rectangle b = ((Control) e.widget).getBounds();
//		System.out.println("TPL: " + new Rectangle(e.x,e.y,e.width, e.height) + " widget " + b);
		e.gc.setBackground(titleSpec.background);
		
		Rectangle r = new Rectangle(b.x+cardConfig.leftMargin, b.y+cardConfig.topMargin, b.width - titleSpec.rightIndent-cardConfig.leftMargin-cardConfig.rightMargin, b.height + cardConfig.cornerRadius-cardConfig.topMargin);
		e.gc.fillRoundRectangle(r.x, r.y, r.width, r.height, cardConfig.cornerRadius, cardConfig.cornerRadius);

		if (titleSpec.icon != null) {
			int iconX = b.x + cardConfig.titleSpacer+cardConfig.leftMargin;
			e.gc.drawImage(titleSpec.icon, iconX, b.y + cardConfig.topMargin+1);
		}
		int leftX = titleSpec.icon == null ? b.x + cardConfig.titleSpacer : b.x + 2 * cardConfig.titleSpacer + titleSpec.icon.getImageData().width;
		e.gc.setClipping(b.x, b.y, b.width - titleSpec.rightIndent - cardConfig.cornerRadius, b.height);
		e.gc.drawText(title, leftX+cardConfig.leftMargin, b.y+cardConfig.topMargin);
		e.gc.setClipping((Rectangle) null);
		e.gc.drawRoundRectangle(r.x-cardConfig.cornerRadiusComp, r.y-cardConfig.cornerRadiusComp, r.width+2*cardConfig.cornerRadiusComp, r.height + 2* cardConfig.cornerRadiusComp, cardConfig.cornerRadius, cardConfig.cornerRadius);
	}

	public void setTitleAndTitleSpec(String title, TitleSpec titleSpec) {
		this.title = title;
		this.titleSpec = titleSpec;
	}

	public TitleSpec getTitleSpec() {
		return titleSpec;
	}

	public String getTitle() {
		return title;
	}
}