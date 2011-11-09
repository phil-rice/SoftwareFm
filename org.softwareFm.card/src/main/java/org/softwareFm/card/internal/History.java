package org.softwareFm.card.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.softwareFm.utilities.collections.Lists;

public class History<T> {

	private final List<T> history = Lists.newList();
	private int index;
	private final Object lock = new Object();
	private final List<IHistoryListener<T>> listeners = new CopyOnWriteArrayList<IHistoryListener<T>>();

	public void push(T newItem) {
		synchronized (lock) {
			if (history.size() > 0 && newItem.equals(history.get(index)))
				return;
			Lists.removeAllAfter(history, index);
			index = history.size();
			history.add(newItem);
			fireListeners(newItem);
		}

	}

	public void addHistoryListener(IHistoryListener<T> listener) {
		listeners.add(listener);
	}
	
	private void fireListeners(T newValue){
		for (IHistoryListener<T> listener: listeners)
			listener.changingTo(newValue);
	}

	public T prev() {
		synchronized (lock) {
			if (index > 0)
				index--;
			T result = history.get(index);
			fireListeners(result);
			return result;
		}
	}

	public T next() {
		synchronized (lock) {
			if (index + 1 < history.size())
				index++;
			T result = history.get(index);
			fireListeners(result);
			return result;
		}
	}

	public boolean hasNext() {
		return index < history.size() - 1;
	}

	public boolean hasPrev() {
		return index > 0;
	}

	public List<T> items() {
		return history;
	}

	public T getItem(int i) {
		return history.get(i);
	}

}