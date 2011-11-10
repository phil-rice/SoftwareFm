package org.softwareFm.card.internal;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.softwareFm.card.gestures.GestureLeftRightListener;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.history.IHistory;

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
		if (history.hasPrev())
			ICallback.Utils.call(callback, history.prev());
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