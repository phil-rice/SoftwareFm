package org.softwareFm.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.runnable.Runnables;
import org.softwareFm.swt.card.composites.TextInBorderWithButtons;
import org.softwareFm.swt.card.dataStore.CardDataStoreFixture;
import org.softwareFm.swt.configuration.CardConfig;
import org.softwareFm.swt.constants.CardConstants;
import org.softwareFm.swt.swt.Swts;

public class TextInBorderWithButtonsMain {
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

