package org.softwareFm.utilities.history;

public interface IHistory<T> {

	void push(T newItem);

	void addHistoryListener(IHistoryListener<T> listener);

	T prev();

	T next();

	boolean hasNext();

	boolean hasPrev();

	T getItem(int i);

}