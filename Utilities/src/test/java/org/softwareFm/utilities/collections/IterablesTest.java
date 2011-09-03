package org.softwareFm.utilities.collections;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.softwareFm.utilities.callbacks.MemoryCallback;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;

public class IterablesTest extends TestCase {
	private final List<Integer> x123 = Arrays.asList(1, 2, 3);
	private final List<Integer> x456 = Arrays.asList(4, 5, 6);
	private final List<Integer> x78 = Arrays.asList(7, 8);

	public void testMap() {
		assertEquals(Arrays.asList(), Iterables.list(Iterables.map(Arrays.<Double> asList(), Functions.times(2))));
		assertEquals(Arrays.asList(2.0d, 4.0d, 6.0d), Iterables.list(Iterables.map(Arrays.asList(1.0, 2.0, 3.0), Functions.times(2))));
	}

	@SuppressWarnings("unchecked")
	public void testFold() {
		assertEquals(1, Iterables.fold(Functions.plusInt(), Collections.EMPTY_LIST, 1).intValue());
		assertEquals(6, Iterables.fold(Functions.plusInt(), x123, 0).intValue());
		assertEquals(7, Iterables.fold(Functions.plusInt(), x123, 1).intValue());
	}

	public void testFilter() {
		checkFilter(Arrays.<Integer> asList(), Functions.even());
		checkFilter(Arrays.asList(1, 3, 5), Functions.even());
		checkFilter(Arrays.asList(1, 2, 3, 4, 5, 6), Functions.even(), 2, 4, 6);
		checkFilter(Arrays.asList(1, 2, 3, 4, 5, 6), Functions.odd(), 1, 3, 5);
	}

	private void checkFilter(List<Integer> asList, IFunction1<Integer, Boolean> acceptor, Integer... expected) {
		assertEquals(Arrays.asList(expected), Iterables.list(Iterables.filter(asList, acceptor)));
	}

	public void testFoldInService() throws InterruptedException, ExecutionException {
		checkFoldInService(0, 0);
		checkFoldInService(0, 0);
		checkFoldInService(6, 0, 1, 2, 3);
		checkFoldInService(7, 1, 1, 2, 3);
		checkFoldInService(66, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
	}

	private void checkFoldInService(Integer expected, Integer initial, Integer... ints) throws InterruptedException, ExecutionException {
		ExecutorService service = new ThreadPoolExecutor(2, 2, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
		try {
			Future<Integer> future = Iterables.fold(service, Arrays.asList(ints), Functions.plusInt(), initial);
			assertEquals(expected, future.get());
		} finally {
			service.shutdown();
		}
	}

	public void testProcessCallback() {
		checkProcessCallback();
		checkProcessCallback(1, 2, 3);

	}

	private void checkProcessCallback(Integer... ints) {
		MemoryCallback<Integer> callback = new MemoryCallback<Integer>();
		Iterables.processCallbacks(Arrays.asList(ints), callback);
		assertEquals(Arrays.asList(ints), callback.getResult());
	}

	public void testList() {
		checkList();
		checkList(1, 2, 3);
	}

	private void checkList(Integer... ints) {
		List<Integer> raw = Arrays.asList(ints);
		assertEquals(raw, Iterables.list(Collections.unmodifiableCollection(raw)));
		assertSame(raw, Iterables.list(raw));
	}

	public void testIterable() {
		checkIterable();
		checkIterable(1, 2, 3);
	}

	private void checkIterable(Integer... ints) {
		List<Integer> raw = Arrays.asList(ints);
		Iterable<Integer> iterable = Iterables.iterable(raw.iterator());
		assertEquals(raw, Iterables.list(iterable));
		assertEquals(Collections.EMPTY_LIST, Iterables.list(iterable));
	}

	@SuppressWarnings("unchecked")
	public void testSplit() throws InterruptedException {
		checkSplit(Arrays.<Integer> asList());
		checkSplit(Arrays.<Integer> asList(1, 2, 3), x123);
		checkSplit(Arrays.<Integer> asList(1, 2, 3, 4, 5, 6), x123, x456);
		for (int i = 0; i < 100; i++)
			checkSplit(Arrays.<Integer> asList(1, 2, 3, 4, 5, 6, 7, 8), x123, x456, x78);
	}

	private void checkSplit(List<Integer> expected, List<Integer>... lists) {
		List<List<Integer>> from = Arrays.asList(lists);
		IFunction1<List<Integer>, Iterable<Integer>> splitFunction = Functions.<List<Integer>, Iterable<Integer>> identity();
		assertEquals(expected, Iterables.list(Iterables.split(from, splitFunction)));
		assertEquals(expected, Iterables.list(Iterables.split(from, splitFunction)));
		assertEquals(expected, Iterables.list(Iterables.split(from, splitFunction)));
		assertEquals(expected, Iterables.list(Iterables.split(from)));
		assertEquals(expected, Iterables.list(Iterables.split(from)));
		assertEquals(expected, Iterables.list(Iterables.split(from)));
	}

}
