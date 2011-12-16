package org.softwareFm.server.sample;

import java.util.concurrent.ExecutionException;

import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.httpClient.response.IResponse;

public class HelloSoftwareFmServer {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		IHttpClient client = IHttpClient.Utils.builder("localhost", 8081);
		
		for (int i = 0; i < 100; i++) {
			final String message = "i: " + i;
			client.post("/a/b/c").addParam("a", Integer.toString(i)).addParam("b", "2").execute(new IResponseCallback() {
				@Override
				public void process(IResponse response) {
					System.out.println(message + ", " + response);
				}
			});
		}
	}
}
