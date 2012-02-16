/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.callbacks;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

public class MemoryCallback<T> implements ICallback<T> {
	private final List<T> result = new ArrayList<T>();

	@Override
	public void process(T t) throws Exception {
		result.add(t);
	}

	public List<T> getResults() {
		return result;
	}

	public T getOnlyResult() {
		Assert.assertEquals(1, result.size());
		return result.get(0);
	}

	public void assertNotCalled() {
		if (result.size() > 0)
			Assert.fail(result.toString());
	}
}