package org.arc4eclipse.utilities.aggregators;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.arc4eclipse.utilities.aggregators.CrossThreadAggregator;
import org.arc4eclipse.utilities.collections.Iterables;
import org.arc4eclipse.utilities.collections.Sets;
import org.arc4eclipse.utilities.functions.IFunction1;

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
			public Thread apply(final List<Integer> from) throws Exception {
				Thread thread = new Thread() {
					private final List<Integer> list = lists.myList();

					
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
