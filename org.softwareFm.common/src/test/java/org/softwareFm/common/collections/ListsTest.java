/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.collections;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;

import org.softwareFm.common.collections.Iterables;
import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.constants.UtilityMessages;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;

public class ListsTest extends TestCase {

	public void testMap() {
		checkMap(Functions.times(2), new Double[] { 1.0, 2.0, 3.0 }, new Double[] { 2.0, 4.0, 6.0 });
		checkMap(Functions.times(2), new Double[] {}, new Double[] {});
	}

	public void testNewList() {
		assertEquals(Collections.EMPTY_LIST, Lists.newList());
	}

	public void testRemoveAllAfter() {
		ArrayList<String> list = new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5"));
		Lists.removeAllAfter(list, 2);
		assertEquals(Arrays.asList("1", "2", "3"), list);
	}

	public void testOrderBy() {
		List<Integer> masterList = Arrays.asList(4, 3, 2, 1, 5, 6, 7);
		List<Integer> sort = Arrays.asList(1, 6, 2, 4);
		Collections.sort(sort, Lists.byListOrder(masterList));
		assertEquals(Arrays.asList(4, 2, 1, 6), sort);
	}

	public void testShuffle() {
		Random random = new Random();
		List<Integer> original = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
		HashSet<Integer> originalAsSet = new HashSet<Integer>(original);
		List<Integer> master = new ArrayList<Integer>(original);
		for (int i = 0; i < 10; i++) {
			List<Integer> newList = Lists.shuffle(random, master);
			assertEquals(original, master);
			assertFalse(original.equals(newList));// this will fail very very rarely 1 in 15! time
			assertEquals(originalAsSet, new HashSet<Integer>(newList));
		}
	}

	private <T> void checkMap(IFunction1<T, T> fn, T[] input, T[] expected) {
		assertEquals(Arrays.asList(expected), Iterables.list(Iterables.map(Arrays.asList(input), fn)));
	}

	public void testPartition() {
		checkPartition(2, new String[0], new String[0], new String[0]);
		checkPartition(2, new String[] { "1", "2", "3", "4" }, new String[] { "1", "3" }, new String[] { "2", "4" });
		checkPartition(3, new String[] { "1", "2", "3", "4", "5", "6" }, new String[] { "1", "4" }, new String[] { "2", "5" }, new String[] { "3", "6" });

		// checkPartitionFails(0);
		checkPartitionFails(-1);
		checkPartitionFails(-10);
	}

	@SuppressWarnings("unchecked")
	public void testPartitionByClass() {
		checkPartitionByClass(Arrays.asList(), Maps.newMap(), Integer.class, String.class);
		checkPartitionByClass(Arrays.asList(1, 2, 3, "4", "5"), Maps.makeMap(Integer.class, Arrays.asList(1, 2, 3), String.class, Arrays.asList("4", "5")), Integer.class, String.class);
		checkPartitionByClass(Arrays.asList(1, 2, 3, "4", "5"), Maps.makeMap(Integer.class, Arrays.asList(1, 2, 3), Object.class, Arrays.asList("4", "5")), Integer.class, Object.class);
	}

	@SuppressWarnings("rawtypes")
	private void checkPartitionByClass(List<? extends Object> input, Map<Object, Object> expected, Class... partitionClasses) {
		Map<Class, ?> actual = Lists.partitionByClass(input, partitionClasses);
		assertEquals(expected, actual);
	}

	@SuppressWarnings("unchecked")
	private void checkPartitionFails(int size) {
		try {
			Lists.partition(size, Collections.EMPTY_LIST);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals(MessageFormat.format(UtilityMessages.needPositivePartitionSize, size), e.getMessage());
		}
	}

	private <T> void checkPartition(int size, T[] input, T[]... expected) {
		List<T>[] actual = Lists.partition(size, Arrays.asList(input));
		assertEquals(actual.length, expected.length);
		for (int i = 0; i < actual.length; i++)
			assertEquals(Arrays.asList(expected[i]), actual[i]);
	}

}