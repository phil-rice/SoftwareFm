/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.swt.configuration.CardConfig;

public class TextInBorderWithClick extends TextInCompositeWithCardMargin {
	
	//TODO this makes the wrong sort of class to be in here, but it is easy to discover. Clean this us
	public static IFunction1<Composite, TextInBorder> makeTextFromString(final int textStyle, final CardConfig cardConfig, final String cardType, final String titleText, final String bodyText, final Runnable whenClicked) {
		return new IFunction1<Composite, TextInBorder>() {
			@Override
			public TextInBorder apply(Composite from) throws Exception {
				TextInBorder result = new TextInBorder(from, textStyle, cardConfig);
				result.setText(cardType, titleText, bodyText);
				if (whenClicked != null)
					result.addClickedListener(whenClicked);
				return result;
			}
		};
	}

	public TextInBorderWithClick(Composite parent, CardConfig cardConfig, final Runnable whenClicked, String cardType, String pattern, Object... args) {
		super(parent, SWT.WRAP, cardConfig);
		setTextFromResourceGetter(cardType, pattern, args);
		addClickedListener(whenClicked);
	}

}