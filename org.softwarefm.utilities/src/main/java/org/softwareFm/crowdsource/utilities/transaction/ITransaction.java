/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.transaction;

import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public interface ITransaction<T> {

	/** waits for the default time out. */
	T get();

	T get(long millisecondsToWait);

	void cancel();

	boolean isCancelled();

	boolean isDone();

	public static class Utils {
		public static <From, To> GatedTransaction<To> gateTransaction(IFunction1<From, To> fn, final From value) {
			return new GatedTransaction<To>(fn, value);

		}

		public static <T> GatedTransaction<T> gateTransaction(final T value) {
			return new GatedTransaction<T>(value);
		}

		public static <T> ITransaction<T> doneTransaction(final T value) {
			return new ITransaction<T>() {
				@Override
				public T get() {
					return value;
				}

				@Override
				public T get(long millisecondsToWait) {
					return value;
				}

				@Override
				public void cancel() {
					throw new UnsupportedOperationException();
				}

				@Override
				public boolean isCancelled() {
					return false;
				}

				@Override
				public boolean isDone() {
					return true;
				}
			};

		}

		public static GatedTransaction<Void> gatedTransaction() {
			return new GatedTransaction<Void>(null);
		}
	}
}