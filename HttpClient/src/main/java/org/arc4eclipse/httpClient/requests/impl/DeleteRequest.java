package org.arc4eclipse.httpClient.requests.impl;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.util.EntityUtils;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.response.impl.Response;
import org.arc4eclipse.utilities.exceptions.WrappedException;

public class DeleteRequest extends AbstractRequestBuilder {

	public DeleteRequest(HttpHost host, HttpClient client, String url) {
		super(host, client, url);
	}

	@Override
	public void execute(IResponseCallback callback) {
		try {
			String param = protocolHostAndUrl();
			HttpDelete delete = new HttpDelete(param);
			HttpResponse httpResponse = client.execute(delete);
			HttpEntity entity = httpResponse.getEntity();
			Response response = new Response(url, httpResponse.getStatusLine().getStatusCode(), entity == null ? null : EntityUtils.toString(entity));
			callback.process(response);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
