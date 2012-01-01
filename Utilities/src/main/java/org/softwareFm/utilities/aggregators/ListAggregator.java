/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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

	@Override
	public void add(T t) {
		result.add(t);
	}

	@Override
	public List<T> result() {
		return result;
	}

}