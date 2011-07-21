package org.arc4eclipse.httpClient.api;

public interface IExceptionListener {

	void exceptionOccured(Throwable throwable);

	public static class Utils {
		public static IExceptionListener syserr() {
			return new IExceptionListener() {

				@Override
				public void exceptionOccured(Throwable throwable) {
					throwable.printStackTrace();
				}
			};
		}
	}
}
