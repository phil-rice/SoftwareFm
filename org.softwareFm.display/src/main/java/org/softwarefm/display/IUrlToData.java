package org.softwarefm.display;

import java.util.Map;

public interface IUrlToData {

	void getData(String entity, String url, Map<String, Object> context, IUrlDataCallback callback);

	static class Utils {

		public static IUrlToData errorCallback() {
			return new IUrlToData() {
				@Override
				public void getData(String entity, String url, Map<String, Object> context, IUrlDataCallback callback) {
					throw new RuntimeException();
				}
			};
		}

	}

}
