package org.arc4eclipse.httpClient.requests;

import org.arc4eclipse.httpClient.response.IResponse;

public interface IResponseCallback<Thing, Aspect> {

	<T> void process(Thing thing, Aspect aspect, IResponse response);

	public static class Utils {
		public static <Thing, Aspect> MemoryResponseCallback<Thing, Aspect> memoryCallback() {
			return new MemoryResponseCallback<Thing, Aspect>();
		}

		public static <Thing, Aspect> IResponseCallback<Thing, Aspect> sysoutStatusCallback() {
			return new IResponseCallback<Thing, Aspect>() {
				@Override
				public <T> void process(Thing thing, Aspect aspect, IResponse response) {
					System.out.println(response.statusCode());
				}
			};
		}
	}
}
