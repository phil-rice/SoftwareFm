/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.httpClient.internal;

import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpRequestBase;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;

public class HeadRequest extends AbstractRequestBuilder {

	public HeadRequest(IServiceExecutor executor, HttpHost host, HttpClient client, List<NameValuePair> defaultHeaders, String url) {
		super(executor, host, client, defaultHeaders, url);
	}

	@Override
	protected HttpRequestBase getRequestBase(String protocolHostAndUrl) {
		HttpHead head = new HttpHead(protocolHostAndUrl);
		return head;
	}
}