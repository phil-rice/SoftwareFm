package org.softwareFm.utilities.history;

import java.util.List;

import org.softwareFm.utilities.collections.Lists;

public class HistoryListenerMock<T> implements IHistoryListener<T> {
	public List<T> values = Lists.newList();

	@Override
	public void changingTo(T newValue) {
		values.add(newValue);
	}
}
