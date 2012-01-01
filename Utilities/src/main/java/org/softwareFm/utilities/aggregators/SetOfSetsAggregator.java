/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.aggregators;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SetOfSetsAggregator<T> implements IAggregator<Set<T>, Set<T>> {

	private Set<Set<T>> result;

	public SetOfSetsAggregator(boolean threadSafe) {
		if (threadSafe)
			result = Collections.synchronizedSet(new HashSet<Set<T>>());
		else
			result = new HashSet<Set<T>>();
	}

	@Override
	public void add(Set<T> t) {
		result.add(t);
	}

	@Override
	public Set<T> result() {
		return new SetFromSets<T>(result);
	}

}