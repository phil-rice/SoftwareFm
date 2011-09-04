package org.softwareFm.repository.api;

import java.util.Map;

public interface IRepositoryStatusListener {

	void statusChanged(String url, RepositoryDataItemStatus status, Map<String, Object> item, Map<String, Object> context) throws Exception;

	public static class Utils {
		public static IRepositoryStatusListener sysout() {
			return new IRepositoryStatusListener() {
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
