package org.softwareFm.arc4eclipseRepository.api;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.softwareFm.arc4eclipseRepository.api.impl.StatusAndData;
import org.softwareFm.utilities.collections.Lists;

public class MemoryStatusChangedListener implements IRepositoryStatusListener {

	private final List<StatusAndData> list = Lists.newList();

	@Override
	public void statusChanged(String url, RepositoryDataItemStatus status, Map<String, Object> item, Map<String, Object> context) throws Exception {
		list.add(new StatusAndData(url, status, item, context));
	}

	@SuppressWarnings("unchecked")
	public void assertEquals(Object... statusAndData) {
		List<StatusAndData> expected = Lists.newList();
		for (int i = 0; i < statusAndData.length; i += 4)
			expected.add(new StatusAndData(//
					(String) statusAndData[i + 0], //
					(RepositoryDataItemStatus) statusAndData[i + 1], //
					(Map<String, Object>) statusAndData[i + 2],//
					(Map<String, Object>) statusAndData[i + 3]));
		Assert.assertEquals(expected, list);
	}
}
