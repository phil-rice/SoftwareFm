/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.client.http.requests.impl;

import org.junit.Test;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.httpClient.internal.ClientBuilder;
import org.softwareFm.crowdsource.httpClient.internal.PostRequest;

public class PostRequestTest extends AbstractRequestTest {
	@Test
	public void testPost() {
		checkRequest(PostRequest.class, builder.post("someUrl"), builder);
		checkRequest(PostRequest.class, builder.post("/someUrl"), builder);
	}

	@Test
	public void testPostWithParameters() {
		ClientBuilder builder = (ClientBuilder) IHttpClient.Utils.builder();
		checkRequest(PostRequest.class, builder.post("someUrl"), builder);
		checkRequest(PostRequest.class, builder.post("someUrl").addParam("n1", "v1"), builder, "n1", "v1");
		checkRequest(PostRequest.class, builder.post("someUrl").addParam("n1", "v1").addParam("n2", "v2"), builder, "n1", "v1", "n2", "v2");
		checkRequest(PostRequest.class, builder.post("someUrl").addParam("n1", "v1").addParams("n2", "v2", "n3", "v3"), builder, "n1", "v1", "n2", "v2", "n3", "v3");
		checkRequest(PostRequest.class, builder.post("/someUrl").addParam("n1", "v1").addParams("n2", "v2", "n3", "v3"), builder, "n1", "v1", "n2", "v2", "n3", "v3");
	}
}