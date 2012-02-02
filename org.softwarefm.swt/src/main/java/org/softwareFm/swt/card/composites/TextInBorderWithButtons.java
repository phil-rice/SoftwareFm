/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.swt.Swts;

public class TextInBorderWithButtons extends TextInBorder {

	private final Composite buttons;

	public TextInBorderWithButtons(Composite parent, int textStyle, final CardConfig cardConfig) {
		super(parent, textStyle, cardConfig);
		buttons = Swts.newComposite(body, SWT.NULL, "Buttons");
		buttons.setLayout(Swts.Row.getHorizonalNoMarginRowLayout());
		body.setLayout(Swts.contentAndButtonBarLayout());
	}

	public void addButton(String text, Runnable runnable) {
		Swts.Buttons.makePushButton(buttons, text, runnable);
		body.layout();
	}

}