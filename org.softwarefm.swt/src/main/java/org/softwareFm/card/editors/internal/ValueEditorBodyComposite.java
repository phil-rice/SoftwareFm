/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.editors.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.title.TitleSpec;
import org.softwareFm.swt.card.internal.CardOutlinePaintListener;
import org.softwareFm.swt.configuration.CardConfig;

public class ValueEditorBodyComposite extends Composite {

	final CardConfig cardConfig;
	final Composite innerBody;
	final TitleSpec titleSpec;

	public ValueEditorBodyComposite(Composite parent, CardConfig cardConfig, TitleSpec titleSpec) {
		super(parent, SWT.NULL);
		this.cardConfig = cardConfig;
		this.titleSpec = titleSpec;
		this.innerBody = new Composite(this, SWT.NULL);
		innerBody.setBackground(titleSpec.background);
		addPaintListener(new CardOutlinePaintListener(titleSpec, cardConfig));

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