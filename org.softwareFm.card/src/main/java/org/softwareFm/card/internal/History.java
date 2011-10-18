package org.softwareFm.card.internal;

import java.util.List;

import org.softwareFm.utilities.collections.Lists;

public class History<T> {

	private final List<T> history = Lists.newList();
	private int index;
	private final Object lock = new Object();

	public void push(T newItem) {
		synchronized (lock) {
			if (history.size() > 0 && newItem.equals(history.get(index)))
				return;
			Lists.removeAllAfter(history, index);
			index = history.size();
			history.add(newItem);
		}

	}

	public T prev() {
		synchronized (lock) {
			if (index > 0)
				index--;
			T result = history.get(index);
			return result;
		}
	}

	public T next() {
		synchronized (lock) {
			if (index + 1 < history.size())
				index++;
			T result = history.get(index);
			return result;
		}
	}
}
