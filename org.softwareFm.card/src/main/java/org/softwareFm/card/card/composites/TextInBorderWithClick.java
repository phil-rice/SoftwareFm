package org.softwareFm.card.card.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.softwareFm.card.configuration.CardConfig;

public class TextInBorderWithClick extends TextInCompositeWithCardMargin {

	public TextInBorderWithClick(Composite parent, CardConfig cardConfig, final Runnable whenClicked, String pattern, Object...args) {
		super(parent, SWT.WRAP , cardConfig);
		setTextFromResourceGetter(pattern, args);
		addClickedListener(whenClicked);
	}

}
