package org.softwareFm.utilities.aggregators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListAggregator<T> implements IAggregator<T, List<T>> {
	private List<T> result;

	public ListAggregator() {
		this(true);
	}

	public ListAggregator(boolean threadSafe) {
		if (threadSafe)
			result = Collections.synchronizedList(new ArrayList<T>());
		else
			result = new ArrayList<T>();
	}

	public void add(T t) {
		result.add(t);
	}

	public List<T> result() {
		return result;
	}

}
