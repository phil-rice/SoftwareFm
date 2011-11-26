/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.httpClient.api.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.util.EntityUtils;
import org.softwareFm.httpClient.api.IHttpGetter;
import org.softwareFm.httpClient.api.IHttpGetterCallback;
import org.softwareFm.httpClient.response.impl.Response;

public class HttpGetter implements IHttpGetter {

	private final ExecutorService service;
	private final DefaultHttpClient client;

	public HttpGetter(ExecutorService service) {
		this.service = service;
		DefaultHttpClient rawClient = new DefaultHttpClient();
		ClientConnectionManager mgr = rawClient.getConnectionManager();
		client = new DefaultHttpClient(new ThreadSafeClientConnManager(mgr.getSchemeRegistry()));
	}

	@Override
	public Future<Response> getFromUrl(String type, final String url, final IHttpGetterCallback callback) {
		return service.submit(new Callable<Response>() {
			@Override
			public Response call() throws Exception {
				HttpGet get = new HttpGet(url.trim());
				HttpResponse httpResponse = client.execute(get);
				HttpEntity entity = httpResponse.getEntity();
				String reply = entity == null ? "" : EntityUtils.toString(entity);

				Response response = new Response(url, httpResponse.getStatusLine().getStatusCode(), reply);
				callback.processGet(url, response);
				return response;
			}
		});
	}

}