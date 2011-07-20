package org.arc4eclipse.arc4eclipseRepository.api;

import org.arc4eclipse.httpClient.response.IResponse;

public interface IArc4EclipseCallback<T> {

	void process(IResponse response, T data);

	public static class Utils {
		@SuppressWarnings("rawtypes")
		public static IArc4EclipseCallback sysout() {
			return new IArc4EclipseCallback() {
				@Override
				public void process(IResponse response, Object data) {
					System.out.println(response.statusCode() + " " + data);
				}
			};
		}

		public static <T1, T2 extends T1> IArc4EclipseCallback<T2> chain(final IArc4EclipseCallback<T1> callback1, final IArc4EclipseCallback<T2> callback2) {
			return new IArc4EclipseCallback<T2>() {
				@Override
				public void process(IResponse response, T2 data) {
					callback1.process(response, data);
					callback2.process(response, data);

				}
			};
		}
	}
}
