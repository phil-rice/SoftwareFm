/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.repository.api;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.softwareFm.repository.api.impl.StatusAndData;
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