package org.arc4eclipse.arc4eclipseRepository.api;

import java.util.Map;

public interface IStatusChangedListener {

	void statusChanged(String url, RepositoryDataItemStatus status, Map<String, Object> item) throws Exception;

	public static class Utils {
		public static IStatusChangedListener sysout() {
			return new IStatusChangedListener() {
				@Override
				public void statusChanged(String url, RepositoryDataItemStatus status, Map<String, Object> item) {
					System.out.println(status + " " + item);
				}
			};
		}

		public static MemoryStatusChangedListener memory() {
			return new MemoryStatusChangedListener();
		};

	}
}
