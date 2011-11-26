/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.callbacks;

import org.softwareFm.utilities.exceptions.WrappedException;

public interface ICallback<T> {
	void process(T t) throws Exception;

	static class Utils {

		public static <T> void call(ICallback<T> callback, T value) {
			try {
				callback.process(value);
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}

		public static <T> ICallback<T> safeSysErrCallback(final ICallback<T> callback) {
			return safeCallback(new ICallback<Throwable>() {
				@Override
				public void process(Throwable t) throws Exception {
					t.printStackTrace();
				}
			}, callback);
		}

		public static ICallback<Throwable> sysErrCallback() {
			return new ICallback<Throwable>() {
				@Override
				public void process(Throwable t) throws Exception {
					t.printStackTrace();
				}
			};
		}

		public static <T> ICallback<T> safeCallback(final ICallback<Throwable> exceptionCallback, final ICallback<T> callback) {
			return new ICallback<T>() {
				@Override
				public void process(T t) throws Exception {
					try {
						callback.process(t);
					} catch (ThreadDeath e) {
						throw e;
					} catch (Throwable e) {
						exceptionCallback.process(e);
					}
				}

			};
		}

		public static final <T> ICallback<T> noCallback() {
			return new NoCallback<T>();
		};

		public static final <T> MemoryCallback<T> memory() {
			return new MemoryCallback<T>();
		};

		public static ICallback<Integer> count = new ICallback<Integer>() {
			@Override
			public void process(Integer count) throws Exception {
				if (count != 0 && count % 50 == 0)
					System.out.println(count);
				count++;
				System.out.print(".");
			}
		};

		public static <T> ICallback<T> sysoutCallback() {
			return new ICallback<T>() {
				@Override
				public void process(T t) throws Exception {
					System.out.println(t);
				}
			};
		}

		public static ICallback<Throwable> rethrow() {
			return new ICallback<Throwable>() {
				@Override
				public void process(Throwable t) throws Exception {
					throw WrappedException.wrap(t);
				}
			};
		}

		public static <T> ICallback<T> exception(final String message) {
			return new ICallback<T>() {
				@Override
				public void process(T t) throws Exception {
					throw new RuntimeException(message);
				}
			};

		}

		public static <T> void processWithWrap(ICallback<T> callback, T value) {
			try {
				callback.process(value);
			} catch (Exception e) {
				throw WrappedException.wrap(e);
			}
		}
	}
}