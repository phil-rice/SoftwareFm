package org.arc4eclipse.arc4eclipseRepository.api;

import java.util.Map;

public interface IStatusChangedListener {

	void statusChanged(String url, RepositoryDataItemStatus status, Map<String, Object> item, Map<String, Object> context) throws Exception;

	public static class Utils {
		public static IStatusChangedListener sysout() {
			return new IStatusChangedListener() {
				@Override
				public void statusChanged(String url, RepositoryDataItemStatus status, Map<String, Object> item, Map<String, Object> context) {
					System.out.println(status + " " + url + " " + item + "  -------------  " + context);
				}

				@Override
				public String toString() {
					return "sysout";
				}
			};
		}

		public static MemoryStatusChangedListener memory() {
			return new MemoryStatusChangedListener();
		};

	}
}
