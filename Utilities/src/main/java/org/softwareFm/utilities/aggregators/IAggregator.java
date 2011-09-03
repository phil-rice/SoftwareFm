package org.softwareFm.utilities.aggregators;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.softwareFm.utilities.functions.IFoldFunction;
import org.softwareFm.utilities.strings.Strings;

/** This is the mutable version of an {@link IFoldFunction} */
public interface IAggregator<From, To> {
	void add(From t);

	To result();

	static class Factory {
		public static <T> IAggregator<T, List<T>> list(boolean threadSafe) {
			return new ListAggregator<T>(threadSafe);
		}

		public static <T> IAggregator<T, String> join(String separator) {
			return Strings.join(separator);
		}

		public static <T> IAggregator<List<T>, Iterable<T>> listToIterable(boolean threadSafe) {
			return new ListsToIterablAggregator<T>(threadSafe);
		}

		public static <T> IAggregator<Set<T>, Set<T>> setOfSetsAggregator(boolean threadSafe) {
			return new SetOfSetsAggregator<T>(threadSafe);
		}

		public static IAggregator<Integer, Integer> sumInts() {
			return new IAggregator<Integer, Integer>() {
				private final AtomicInteger sum = new AtomicInteger();

				@Override
				public void add(Integer t) {
					sum.addAndGet(t);

				}

				@Override
				public Integer result() {
					return sum.get();
				}
			};
		}

		public static IAggregator<Long, Long> sumLongs() {
			return new IAggregator<Long, Long>() {
				private final AtomicLong sum = new AtomicLong();

				@Override
				public void add(Long t) {
					sum.addAndGet(t);

				}

				@Override
				public Long result() {
					return sum.get();
				}
			};
		}

	}

	@SuppressWarnings("rawtypes")
	static class CallableFactory {
		private static Callable listNotThreadSafe = new Callable<IAggregator<Object, List<Object>>>() {
			@Override
			public IAggregator<Object, List<Object>> call() throws Exception {
				return Factory.<Object> list(false);
			}
		};

		private static Callable listThreadSafe = new Callable<IAggregator<Object, List<Object>>>() {
			@Override
			public IAggregator<Object, List<Object>> call() throws Exception {
				return Factory.<Object> list(true);
			}
		};

		@SuppressWarnings("unchecked")
		public static <T> Callable<IAggregator<T, List<T>>> list(boolean threadSafe) {
			return threadSafe ? listThreadSafe : listNotThreadSafe;
		}

		public static <T> Callable<IAggregator<Set<T>, Set<T>>> setToSetAggregator(final boolean threadSafe) {
			return new Callable<IAggregator<Set<T>, Set<T>>>() {
				@Override
				public IAggregator<Set<T>, Set<T>> call() throws Exception {
					return Factory.setOfSetsAggregator(threadSafe);
				}
			};
		}

		public static Callable<IAggregator<Long, Long>> sum() {
			return new Callable<IAggregator<Long, Long>>() {
				@Override
				public IAggregator<Long, Long> call() throws Exception {
					return Factory.sumLongs();
				}
			};
		}

		public static Callable<IAggregator<Integer, Integer>> sumInts() {
			return new Callable<IAggregator<Integer, Integer>>() {
				@Override
				public IAggregator<Integer, Integer> call() throws Exception {
					return Factory.sumInts();
				}
			};
		}
	}

}
