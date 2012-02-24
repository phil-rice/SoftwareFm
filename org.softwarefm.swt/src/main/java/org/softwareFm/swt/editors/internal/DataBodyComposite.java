/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.editors.internal;

import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swt.card.CardOutlinePaintListener;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.title.TitleSpec;

public class DataBodyComposite extends Composite {

	public 	final CardConfig cardConfig;
	public final Composite innerBody;

	public DataBodyComposite(Composite parent, CardConfig cardConfig,Callable<TitleSpec> titleSpecGetter) {
		super(parent, SWT.NULL);
		this.cardConfig = cardConfig;
		this.innerBody = new Composite(this, SWT.NULL);
		addPaintListener(new CardOutlinePaintListener(cardConfig, titleSpecGetter));

	}

	@Override
	public Rectangle getClientArea() {
		// note that the topMargin doesn't reference this component: it affects the space between the top of somewhere and the title.
		// There is a two pixel gap between the top of the card and the title
		Rectangle clientArea = super.getClientArea();
		int cardWidth = clientArea.width - cardConfig.rightMargin - cardConfig.leftMargin;
		int cardHeight = clientArea.height - cardConfig.bottomMargin - 2;
		Rectangle result = new Rectangle(clientArea.x + cardConfig.leftMargin, clientArea.y + 2, cardWidth, cardHeight);
		return result;
	}

	public Composite getInnerBody() {
		return innerBody;
	}

}