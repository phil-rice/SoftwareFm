package org.softwareFm.card.navigation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.internal.History;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.callbacks.ICallback;

public class NavHistoryCombo implements IHasControl {

	 final Combo combo;
	private final History<String> history;

	public NavHistoryCombo(Composite composite, History <String>history, final ICallback<String> callbackToGotoUrl) {
		this.history = history;
		combo = new Combo(composite, SWT.DROP_DOWN|SWT.NO_FOCUS);
		combo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = combo.getSelectionIndex();
				if (index == -1)
					return;
				ICallback.Utils.call(callbackToGotoUrl, combo.getItem(index));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				ICallback.Utils.call(callbackToGotoUrl, combo.getText());
			}
		});
	}
	

	@Override
	public Control getControl() {
		return combo;
	}


	public void updateFromHistory() {
		combo.removeAll();
		for (String item: history.items())
			combo.add(item);
		
	}

}
