/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.navigation.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.history.IHistory;
import org.softwareFm.common.history.IHistoryListener;
import org.softwareFm.swt.composites.IHasControl;

public class NavHistoryCombo<T> implements IHasControl {

	final Combo combo;
	private final IFunction1<T, String> stringFn;
	private boolean changing;

	public NavHistoryCombo(Composite composite, final IHistory<T> history, final ICallback<T> callbackToGotoUrl, NavNextHistoryPrevConfig<T> config) {
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