package org.softwareFm.card.navigation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.softwareFm.card.api.ICard;
import org.softwareFm.utilities.functions.Functions;

public class NavTitle implements ITitleBarForCard {

	private final Label label;

	public NavTitle(Composite parent, String initialLabel) {
		label = new Label(parent, SWT.NULL);
		label.setText(initialLabel);
	}

	@Override
	public void setUrl(ICard card) {
		label.setText(Functions.call(card.cardConfig().cardTitleFn,card.url()));
	}

	@Override
	public Control getControl() {
		return label;
	}

}
