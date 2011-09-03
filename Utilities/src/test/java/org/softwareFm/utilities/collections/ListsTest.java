package org.softwareFm.utilities.collections;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.utilities.constants.UtilityMessages;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.maps.Maps;

public class ListsTest extends TestCase {

	public void testMap() {
		checkMap(Functions.times(2), new Double[] { 1.0, 2.0, 3.0 }, new Double[] { 2.0, 4.0, 6.0 });
		checkMap(Functions.times(2), new Double[] {}, new Double[] {});
	}

	public void testNewList() {
		assertEquals(Collections.EMPTY_LIST, Lists.newList());
	}

	private <T> void checkMap(IFunction1<T, T> fn, T[] input, T[] expected) {
		assertEquals(Arrays.asList(expected), Iterables.list(Iterables.map(Arrays.asList(input), fn)));
	}

	public void testPartition() {
		checkPartition(2, new String[0], new String[0], new String[0]);
		checkPartition(2, new String[] { "1", "2", "3", "4" }, new String[] { "1", "3" }, new String[] { "2", "4" });
		checkPartition(3, new String[] { "1", "2", "3", "4", "5", "6" }, new String[] { "1", "4" }, new String[] { "2", "5" }, new String[] { "3", "6" });

//		checkPartitionFails(0);
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
