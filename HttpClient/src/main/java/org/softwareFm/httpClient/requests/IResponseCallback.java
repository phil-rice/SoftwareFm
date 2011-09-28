package org.softwareFm.httpClient.requests;

import org.softwareFm.httpClient.response.IResponse;

public interface IResponseCallback {

	void process(IResponse response);

	public static class Utils {
		public static IResponseCallback noCallback(){
			return new IResponseCallback() {
				@Override
				public void process(IResponse response) {
				}
			};
		}
		
		public static <Thing, Aspect> MemoryResponseCallback<Thing, Aspect> memoryCallback() {
			return new MemoryResponseCallback<Thing, Aspect>();
		}

		public static IResponseCallback sysoutStatusCallback() {
			return new IResponseCallback() {
				@Override
				public void process(IResponse response) {
					System.out.println(response.url() + " " + response.statusCode());
				}
			};
		}
	}
}
