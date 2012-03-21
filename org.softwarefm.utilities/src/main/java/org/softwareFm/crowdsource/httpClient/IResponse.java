/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.httpClient;

import org.softwareFm.crowdsource.httpClient.internal.Response;



public interface IResponse {

	String url();

	int statusCode();

	String asString();

	String mimeType();

	public static class Utils {

		public static IResponse create(final String url, final int statusCode, final String string, final String mimeType) {
			return new Response(statusCode, url, string, mimeType);
		}

		public static IResponse okText(final String url, final String string) {
			return new Response(200, url, string, "text/plain");
		}

	}

}