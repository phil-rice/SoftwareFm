package org.arc4eclipse.utilities.callbacks;

public interface ICallback<T> {
	void process(T t) throws Exception;

	static class Utils {
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
	}
}
