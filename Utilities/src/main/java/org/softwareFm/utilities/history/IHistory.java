package org.softwareFm.utilities.history;


public interface IHistory<T> extends Iterable<T>{

	void push(T newItem);

	void addHistoryListener(IHistoryListener<T> listener);

	T previous();

	T next();

	boolean hasNext();

	boolean hasPrevious();

	T getItem(int i);

	int size();


}