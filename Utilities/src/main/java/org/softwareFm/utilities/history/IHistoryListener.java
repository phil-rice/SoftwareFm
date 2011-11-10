package org.softwareFm.utilities.history;

public interface IHistoryListener<T> {

	void changingTo(T newValue);
	
}
