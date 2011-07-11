package org.arc4eclipse.httpClient.api.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.arc4eclipse.httpClient.api.IHttpClient;
import org.arc4eclipse.httpClient.requests.IRequestBuilder;
import org.arc4eclipse.httpClient.requests.IResponseCallback;
import org.arc4eclipse.httpClient.response.impl.Response;
import org.arc4eclipse.utilities.exceptions.WrappedException;

public class HttpClientMock implements IHttpClient, IRequestBuilder {

	public final String expectedUrl;
	public final String reply;
	public final boolean postOrNotGet;
	public int count;
	public final int status;
	public final List<NameValuePair> params = new ArrayList<NameValuePair>();

	public HttpClientMock(boolean postOrNotGet, String expectedUrl, int status, String reply) {
		this.postOrNotGet = postOrNotGet;
		this.expectedUrl = expectedUrl;
		this.status = status;
		this.reply = reply;

	}

	
	public IRequestBuilder post(String url) {
		Assert.assertEquals(expectedUrl, url);
		Assert.assertEquals(1, count++);
		return this;
	}

	
	public IRequestBuilder get(String url) {
		Assert.assertEquals(expectedUrl, url);
		Assert.assertFalse(postOrNotGet);
		Assert.assertEquals(0, count++);
		return this;
	}

	
	public <Context1, Context2> void execute(Context1 context1, Context2 context2, IResponseCallback<Context1, Context2> callback) {
		try {
			callback.process(context1, context2, new Response(status, reply));
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	
	public IRequestBuilder addParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
		return this;
	}

	
	public IRequestBuilder addParams(String... nameAndValue) {
		for (int i = 0; i < nameAndValue.length; i += 2)
			params.add(new BasicNameValuePair(nameAndValue[i + 0], nameAndValue[i + 1]));
		return this;
	}

}
