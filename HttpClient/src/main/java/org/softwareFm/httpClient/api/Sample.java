package org.softwareFm.httpClient.api;

import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.IResponse;

public class Sample {

	public static void main(String[] args) {
		IHttpClient client = IHttpClient.Utils.defaultClient();
		try {
			client.get("projects/934/javaruntime.Hello").addHeader("SoftwareFm", "value").execute(new IResponseCallback() {
				@Override
				public void process(IResponse response) {
					System.out.println(response);
				}
			});
		} finally {
			client.shutdown();
		}
	}
}
