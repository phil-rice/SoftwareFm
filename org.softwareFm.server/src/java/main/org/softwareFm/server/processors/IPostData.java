package org.softwareFm.server.processors;

import java.util.Map;

import org.softwareFm.server.IGitServer;

public interface IPostData {
	void post(String url, Map<String, Object> data);

	public static class Utils {
		
		public static IPostData rawPost(final IGitServer gitServer){
			return new IPostData() {
				@Override
				public void post(String url, Map<String, Object> data) {
					gitServer.post(url, data);
				}
			};
		}
		
	}
}
