/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utililties.aggregators;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFoldFunction;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.strings.Strings;

/** This is the mutable version of an {@link IFoldFunction} */
public interface IAggregator<From, To> {
	void add(From t);

	To result();

	abstract public static class Utils {

		public static <T> IAggregator<T, String> join(String separator) {
			return Strings.join(separator);
		}

		public static <T> IAggregator<T, String> join(final IFunction1<T, String> mapFn, final String separator) {
			return new IAggregator<T, String>() {
				private final StringBuilder builder = new StringBuilder();
				private boolean addSeparator;

				@Override
				public void add(T t) {
					if (addSeparator)
						builder.append(separator);
					addSeparator = true;
					String string = Functions.call(mapFn, t);
					builder.append(string);
				}

				@Override
				public String result() {
					return builder.toString();
				}
			};
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

	abstract static class CallableFactory {

		public static Callable<IAggregator<Long, Long>> sum() {
			return new Callable<IAggregator<Long, Long>>() {
				@Override
				public IAggregator<Long, Long> call() throws Exception {
					return Utils.sumLongs();
				}
			};
		}

		public static Callable<IAggregator<Integer, Integer>> sumInts() {
			return new Callable<IAggregator<Integer, Integer>>() {
				@Override
				public IAggregator<Integer, Integer> call() throws Exception {
					return Utils.sumInts();
				}
			};
		}
	}

}