/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.card.gestures;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.history.IHistory;

public class HistoryGestureListener<T> extends GestureLeftRightListener {

	private final Control control;
	private final IHistory<T> history;
	private final ICallback<T> callback;
	private Point start;

	public HistoryGestureListener(Control control, int sensitivity, int start, IHistory<T> history, ICallback<T> callback) {
		super(control, sensitivity, start);
		this.control = control;
		this.history = history;
		this.callback = callback;
	}

	@Override
	public void goLeft() {
		if (history.hasPrevious())
			ICallback.Utils.call(callback, history.previous());
	}

	@Override
	public void goRight() {
		if (history.hasNext())
			ICallback.Utils.call(callback, history.next());
	}

	@Override
	public void starting() {
		if (!control.isDisposed())
			start = control.getLocation();
	}

	@Override
	public void moving(int delta) {
		if (!control.isDisposed())
			control.setLocation(start.x - delta / 2, start.y);
	}

	@Override
	public void stopping() {
		if (!control.isDisposed())
			if (start != null)
				control.setLocation(start);

	}
}