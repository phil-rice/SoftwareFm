package org.softwarefm.utilities.events.internal;

import org.softwarefm.utilities.callbacks.ICallback;
import org.softwarefm.utilities.events.IListenerList;
import org.softwarefm.utilities.events.IMultipleListenerList;

public class ListenerList<L> implements IListenerList<L> {

	private final IMultipleListenerList list;
	private final Object source;
	private final Class<L> clazz;

	public ListenerList(IMultipleListenerList list, Class<L> clazz, Object source) {
		this.list = list;
		this.clazz = clazz;
		this.source = source;
	}

	public void addListener(L listener) {
		list.addListener(source, clazz, listener);
	}

	public void fire(ICallback<L> callback) {
		list.fire(source, clazz, callback);
	}

	public void removeListener(L listener) {
		list.removeListener(source, clazz, listener);

	}

	@Override
	public void clear() {
		list.clear(source, clazz);
	}

	@Override
	public boolean contains(L listener) {
		return list.contains(source, clazz, listener);
	}

	@Override
	public int size() {
		return list.size(source, clazz);
	}

}
