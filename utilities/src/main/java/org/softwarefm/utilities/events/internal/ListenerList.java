package org.softwarefm.utilities.events.internal;

import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IListenerList;
import org.softwarefm.utilities.events.IMultipleListenerList;

public class ListenerList<L> implements IListenerList<L> {

	private final IMultipleListenerList list;
	private final Object source;

	public ListenerList(IMultipleListenerList list, Object source) {
		this.list = list;
		this.source = source;
	}

	public void addListener(L listener) {
		list.addListener(source, listener);
	}

	public void fire(ICallback<L> callback) {
		list.fire(source, callback);
	}


}
