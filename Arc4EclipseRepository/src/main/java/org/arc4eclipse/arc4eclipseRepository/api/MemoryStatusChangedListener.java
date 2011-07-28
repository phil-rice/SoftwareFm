package org.arc4eclipse.arc4eclipseRepository.api;

import java.util.List;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.api.impl.StatusAndData;
import org.arc4eclipse.utilities.collections.Lists;
import org.junit.Assert;

public class MemoryStatusChangedListener implements IStatusChangedListener {

	private final List<StatusAndData> list = Lists.newList();

	@Override
	public void statusChanged(String url, RepositoryDataItemStatus status, Map<String, Object> item) throws Exception {
		list.add(new StatusAndData(url, status, item));
	}

	@SuppressWarnings("unchecked")
	public void assertEquals(Object... statusAndData) {
		List<StatusAndData> expected = Lists.newList();
		for (int i = 0; i < statusAndData.length; i += 4)
			expected.add(new StatusAndData(//
					(String) statusAndData[i + 0], //
					(RepositoryDataItemStatus) statusAndData[i + 1], //
					(Map<String, Object>) statusAndData[i + 2]));
		Assert.assertEquals(expected, list);
	}

}
