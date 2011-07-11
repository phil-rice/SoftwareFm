package org.arc4eclipse.utilities.callbacks;

public interface ICallback2<T1, T2> {
	void process(T1 first, T2 second) throws Exception;

	static class Utils {
		public static final <T1, T2> ICallback2<T1, T2> noCallback() {
			return new NoCallback2<T1, T2>();
		};

	}
}
