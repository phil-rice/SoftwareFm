package org.softwareFm.card.navigation;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.api.ICardDataStore;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.callbacks.ICallback;

public class NavCombo implements IHasControl {

	private Combo combo;

	public NavCombo(Composite parent, ICardDataStore cardDataStore, final String rootUrl, final ICallback<String> callbackToGotoUrl) {
		combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectionIndex = combo.getSelectionIndex();
				if (selectionIndex == -1) {
					return;
				}
				String postFix = combo.getItem(selectionIndex);
				String newUrl = rootUrl + "/" + postFix;
				ICallback.Utils.call(callbackToGotoUrl, newUrl);
			}
		});

	}

	public void setDropdownItems(List<String> items) {
		combo.removeAll();
		for (String item: items)
			combo.add(item);
	}

	@Override
	public Control getControl() {
		return combo;
	}
}
