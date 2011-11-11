package org.softwareFm.utilities.callbacks;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

public class MemoryCallback<T> implements ICallback<T> {
	private final List<T> result = new ArrayList<T>();

	@Override
	public void process(T t) throws Exception {
		result.add(t);
	}

	public List<T> getResult() {
		return result;
	}

	public T getOnlyResult() {
		Assert.assertEquals(1, result.size());
		return result.get(0);
	}

	public void assertNotCalled() {
		if (result.size()>0)
			Assert.fail(result.toString());
	}
}