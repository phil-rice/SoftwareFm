/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.collections;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

public class Sets {

	public static <T> Set<T> makeSet(T... ts) {
		return new HashSet<T>(Arrays.asList(ts));
	}

	public static <T> Set<T> makeImmutableSet(T... ts) {
		return Collections.unmodifiableSet(new HashSet<T>(Arrays.asList(ts)));
	}

	public static <T> Set<T> set(Iterable<T> iterable) {
		Set<T> result = new HashSet<T>();
		for (T t : iterable)
			result.add(t);
		return result;
	}

	public static <T1, T2> void assertMatches(Iterable<T1> left, Iterable<T2> right) {
		Assert.assertEquals(Sets.set(left), Sets.set(right));

	}

	public static <T> Set<T> from(ISimpleList<T> simpleList) {
		Set<T> result = Sets.newSet();
		for (int i = 0; i < simpleList.size(); i++)
			result.add(simpleList.get(i));
		return result;
	}

	public static <T> Set<T> newSet() {
		return new HashSet<T>();
	}

	public static String getOnly(Set<String> set) {
		if (set.size()!= 1)
			throw new IllegalArgumentException(set.toString());
		return set.iterator().next();
	}
}