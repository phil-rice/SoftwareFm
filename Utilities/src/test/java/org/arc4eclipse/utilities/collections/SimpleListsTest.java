package org.arc4eclipse.utilities.collections;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.TestCase;

import org.arc4eclipse.utilities.aggregators.IAggregator;
import org.arc4eclipse.utilities.collections.ISimpleList;
import org.arc4eclipse.utilities.collections.SimpleLists;
import org.arc4eclipse.utilities.functions.Functions;
import org.arc4eclipse.utilities.functions.IFunction1;
import org.arc4eclipse.utilities.functions.ISymmetricFunction;
import org.codehaus.cake.forkjoin.ForkJoinPool;

public class SimpleListsTest extends TestCase {

	public void testSimpleList() {
		checkSimpleList();
		checkSimpleList(1, 2, 3);
	}

	private void checkSimpleList(Integer... ints) {
		checkEquals(SimpleLists.simpleList(ints), ints);
	}

	public void testFromList() {
		checkFromList();
		checkFromList(1, 2, 3);
	}

	private void checkFromList(Integer... ints) {
		checkEquals(SimpleLists.fromList(Arrays.asList(ints)), ints);
	}

	public void testMapReduce_And_MapAggregate_And_Map_Two_Aggregators() throws InterruptedException, ExecutionException {
		checkMapReduce(10, Functions.timesInt(1), 1, 2, 3, 4);
		checkMapReduce(20, Functions.timesInt(2), 1, 2, 3, 4);
		checkMapReduce(240, Functions.timesInt(2), 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
	}

	private void checkMapReduce(int expected, IFunction1<Integer, Integer> mapFn, Integer... ints) throws InterruptedException, ExecutionException {
		ForkJoinPool pool = new ForkJoinPool();
		ISimpleList<Integer> from = SimpleLists.simpleList(ints);
		ISymmetricFunction<Integer> plus = Functions.plusInt();

		Future<Integer> futureForMapReduce = SimpleLists.<Integer, Integer, Integer> mapReduce(3, pool, from, plus, plus, mapFn, 0);
		assertEquals(expected, futureForMapReduce.get().intValue());

		Future<IAggregator<Integer, Integer>> futureForMapAggregate = SimpleLists.mapAggregate(3, pool, from, IAggregator.Factory.sumInts(), mapFn);
		assertEquals(expected, futureForMapAggregate.get().result().intValue());

		Future<IAggregator<Integer, Integer>> futureForTwoAggregators = SimpleLists.mapTwoAggregators(3, pool, from, IAggregator.Factory.sumInts(), IAggregator.CallableFactory.sumInts(), mapFn);
		assertEquals(expected, futureForTwoAggregators.get().result().intValue());
		pool.shutdown();
	}

	public void testFoldInRange() {
		checkFoldInRange(6, 0, 3, 0, /* ints */1, 2, 3);
		checkFoldInRange(7, 0, 3, 1, /* ints */1, 2, 3);
		checkFoldInRange(5, 1, 3, 0, /* ints */1, 2, 3);
		checkFoldInRange(1, 0, 1, 0, /* ints */1, 2, 3);
		checkFoldInRange(0, 0, 0, 0, /* ints */1, 2, 3);
	}

	private void checkFoldInRange(int expected, int low, int high, int initial, Integer... ints) {
		assertEquals(expected, SimpleLists.foldInRange(SimpleLists.simpleList(ints), low, high, Functions.plusInt(), initial).intValue());
	}

	private void checkEquals(ISimpleList<Integer> simpleList, Integer... ints) {
		assertEquals(ints.length, simpleList.size());
		for (int i = 0; i < ints.length; i++)
			assertEquals(ints[i], simpleList.get(i));
		assertEquals(Arrays.asList(ints), SimpleLists.asList(simpleList));
	}

}
