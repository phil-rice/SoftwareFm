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
	}
}
