/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.title;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.swt.composites.IHasControl;
import org.softwareFm.swt.configuration.CardConfig;

public class TitleWithTitlePaintListener implements IHasControl {

	public final Canvas canvas;
	private final TitlePaintListener listener;

	public TitleWithTitlePaintListener(Composite parent, final CardConfig cardConfig, final TitleSpec initialTitleSpec, String initialTitle, String initialTooltip) {
		canvas = new Canvas(parent, SWT.NONE) {
			@Override
			public Rectangle getClientArea() {
				Rectangle ca = super.getClientArea();
				return new Rectangle(ca.x + cardConfig.leftMargin, ca.y + cardConfig.topMargin, ca.width - cardConfig.leftMargin - cardConfig.rightMargin, ca.height - cardConfig.topMargin);
			}
		};
		canvas.setToolTipText(initialTooltip);
		listener = new TitlePaintListener(cardConfig, initialTitleSpec, initialTitle);
		canvas.addPaintListener(listener);
	}

	public void setTitleAndImage(String title, String tooltip, TitleSpec titleSpec) {
		canvas.setToolTipText(tooltip);
		listener.setTitleAndTitleSpec(title, titleSpec);
		canvas.redraw();
	}

	public String getText() {
		return listener.getTitle();
	}

	public TitleSpec getTitleSpec() {
		return listener.getTitleSpec();
	}

	@Override
	public Control getControl() {
		return canvas;
	}

}