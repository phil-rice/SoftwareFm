package org.arc4eclipse.displayCore.api;

public interface ICodec<T> {

	T fromString(String encoded);

	String toString(T nameAndValue);

	public static class Utils {

		public static <T> ICodec<T> errorEncoder() {
			return new ICodec<T>() {
				@Override
				public T fromString(String encoded) {
					throw new UnsupportedOperationException();
				}

				@Override
				public String toString(T nameAndValue) {
					throw new UnsupportedOperationException();
				}
			};
		}

		public static ICodec<String> identityEncoder() {
			return new ICodec<String>() {

				@Override
				public String fromString(String encoded) {
					return encoded;
				}

				@Override
				public String toString(String nameAndValue) {
					return nameAndValue;
				}
			};
		}

	}
}
