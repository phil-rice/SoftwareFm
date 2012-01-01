/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.configuration.CardConfig;

public class TextInBorderWithClick extends TextInCompositeWithCardMargin {

	public TextInBorderWithClick(Composite parent, CardConfig cardConfig, final Runnable whenClicked, String cardType, String pattern, Object... args) {
		super(parent, SWT.WRAP, cardConfig);
		setTextFromResourceGetter(cardType, pattern, args);
		addClickedListener(whenClicked);
	}

}