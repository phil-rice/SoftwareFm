/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.client.http.api;

import java.util.Arrays;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.client.http.response.IResponse;

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