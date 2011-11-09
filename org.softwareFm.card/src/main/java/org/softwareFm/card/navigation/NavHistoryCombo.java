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
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;

public class NavHistoryCombo<T> implements IHasControl {

	final Combo combo;
	private final History<T> history;
	private final IFunction1<T, String> stringFn;

	public NavHistoryCombo(Composite composite, final History<T> history, final ICallback<T> callbackToGotoUrl, NavNextHistoryPrevConfig<T> config) {
		this.history = history;
		this.stringFn = config.stringFn;
		combo = new Combo(composite, SWT.DROP_DOWN | SWT.NO_FOCUS|SWT.READ_ONLY);
		combo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = combo.getSelectionIndex();
				if (index == -1)
					return;
				ICallback.Utils.call(callbackToGotoUrl, history.getItem(index));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	@Override
	public Control getControl() {
		return combo;
	}

	public void updateFromHistory() {
		combo.removeAll();
		for (T item : history.items())
			combo.add(Functions.call(stringFn, item));
		combo.setText(Functions.call(stringFn, history.prev()));

	}

}
