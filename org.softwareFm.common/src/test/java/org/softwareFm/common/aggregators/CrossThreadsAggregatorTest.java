/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.aggregators;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.softwareFm.common.aggregators.CrossThreadAggregator;
import org.softwareFm.common.collections.Iterables;
import org.softwareFm.common.collections.Sets;
import org.softwareFm.common.functions.IFunction1;

public class CrossThreadsAggregatorTest extends TestCase {
	private final List<Integer> x123 = Arrays.asList(1, 2, 3);
	private final List<Integer> x456 = Arrays.asList(4, 5, 6);
	private final List<Integer> x78 = Arrays.asList(7, 8);

	@SuppressWarnings("unchecked")
	public void testIterableFromListsTest() throws InterruptedException {
		check(Arrays.<Integer> asList());
		check(Arrays.<Integer> asList(1, 2, 3), x123);
		check(Arrays.<Integer> asList(1, 2, 3, 4, 5, 6), x123, x456);
		for (int i = 0; i < 100; i++)
			check(Arrays.<Integer> asList(1, 2, 3, 4, 5, 6, 7, 8), x123, x456, x78);
	}

	private void check(List<Integer> expected, List<Integer>... ints) throws InterruptedException {
		final CrossThreadAggregator<Integer> lists = new CrossThreadAggregator<Integer>(ints.length);
		Iterable<Thread> threads = Iterables.map(Arrays.asList(ints), new IFunction1<List<Integer>, Thread>() {
			@Override
			public Thread apply(final List<Integer> from) throws Exception {
				Thread thread = new Thread() {
					private final List<Integer> list = lists.myList();

					@Override
					public void run() {
						for (int i : from)
							list.add(i);
					}
				};
				thread.start();
				return thread;
			}
		});
		for (Thread thread : threads)
			thread.join();
		assertEquals(Sets.set(expected), Sets.set(lists));
		assertEquals(Sets.set(expected), Sets.set(lists));
	}
}