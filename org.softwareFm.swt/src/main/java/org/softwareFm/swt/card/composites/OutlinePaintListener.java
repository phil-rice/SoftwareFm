/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.composites;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.softwareFm.swt.configuration.CardConfig;

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