/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.card.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.configuration.CardConfig;
import org.softwareFm.card.constants.CardConstants;
import org.softwareFm.card.dataStore.CardDataStoreFixture;
import org.softwareFm.display.swt.Swts;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.runnable.Runnables;

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

	public static void main(String[] args) {
		Swts.Show.display(TextInBorderWithButtons.class.getSimpleName(), new IFunction1<Composite, Composite>() {
			@Override
			public Composite apply(Composite from) throws Exception {
				CardConfig cardConfig = CardDataStoreFixture.syncCardConfig(from.getDisplay());
				TextInBorderWithButtons textInBorderWithButtons = new TextInBorderWithButtons(from, SWT.WRAP | SWT.READ_ONLY, cardConfig);
				textInBorderWithButtons.setText(CardConstants.artifact, "title", "here\nis\nsome\ntext");
				textInBorderWithButtons.addButton("first", Runnables.sysout("first"));
				textInBorderWithButtons.addButton("second", Runnables.sysout("second"));
				return (Composite) textInBorderWithButtons.getControl();
			}
		});
	}
}