package org.softwareFm.httpClient.api;

import java.util.Arrays;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.IResponse;

public class Sample {

	public static void main(String[] args) {
		IHttpClient client = IHttpClient.Utils.defaultClient();
		try {
			client.setDefaultHeaders(Arrays.<NameValuePair> asList(new BasicNameValuePair("SoftwareFm", "Value")));
			client.get("groups/934/javaruntime.sfm").execute(new IResponseCallback() {
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
