package org.arc4eclipse.arc4eclipseRepository.api;

import java.util.List;

import org.arc4eclipse.arc4eclipseRepository.api.impl.StatusAndData;
import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;
import org.arc4eclipse.utilities.collections.Lists;
import org.junit.Assert;

public class MemoryStatusChangedListener<T extends IRepositoryDataItem> implements IStatusChangedListener<T> {

	private final List<StatusAndData> list = Lists.newList();

	@Override
	public void statusChanged(String url, java.lang.Class<? extends T> clazz, RepositoryDataItemStatus status, T item) {
		list.add(new StatusAndData(url, clazz, status, item));
	}

	@SuppressWarnings("unchecked")
	public void assertEquals(Object... statusAndData) {
		List<StatusAndData> expected = Lists.newList();
		for (int i = 0; i < statusAndData.length; i += 4)
			expected.add(new StatusAndData(//
					(String) statusAndData[i + 0], //
					(Class<?>) statusAndData[i + 1], //
					(RepositoryDataItemStatus) statusAndData[i + 2], //
					(T) statusAndData[i + 3]));
		Assert.assertEquals(expected, list);
	}

}
