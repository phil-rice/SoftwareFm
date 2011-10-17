package org.softwareFm.card.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.ICardAndChild;

public class CardAndChild implements ICardAndChild{


	public CardAndChild(Composite parent) {
		SashForm holder = new SashForm(parent, SWT.VERTICAL);
	}
	
	@Override
	public Control getControl() {
		return null;
	}

}
