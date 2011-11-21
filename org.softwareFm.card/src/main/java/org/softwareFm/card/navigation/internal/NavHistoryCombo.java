package org.softwareFm.card.navigation.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.display.composites.IHasControl;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.history.IHistory;
import org.softwareFm.utilities.history.IHistoryListener;

public class NavHistoryCombo<T> implements IHasControl {

	final Combo combo;
	private final IHistory<T> history;
	private final IFunction1<T, String> stringFn;
	private boolean changing;

	public NavHistoryCombo(Composite composite, final IHistory<T> history, final ICallback<T> callbackToGotoUrl, NavNextHistoryPrevConfig<T> config) {
		this.history = history;
		this.stringFn = config.stringFn;
		combo = new Combo(composite, SWT.DROP_DOWN | SWT.NO_FOCUS | SWT.READ_ONLY);
		combo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (changing)
					return;
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
		history.addHistoryListener(new IHistoryListener<T>() {
			@Override
			public void changingTo(T newValue) {
				changing = true;
				try {
					combo.removeAll();
					for (T item : history)
						combo.add(Functions.call(stringFn, item));
					combo.setText(Functions.call(stringFn, history.last()));
				} finally {
					changing = false;
				}
			}
		});
	}

	@Override
	public Control getControl() {
		return combo;
	}


}
