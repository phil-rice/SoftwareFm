/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.repositoryFacard;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.httpClient.requests.IResponseCallback;

public interface IRepositoryFacard extends IRepositoryFacardReader {

	/** adds this as an Http header to all requests */
	void addHeader(String name, String value);

	/** This will delete the node at the Url */
	Future<?> delete(String url, IResponseCallback callback);

	/** This will create or update the node at the location. The map <em>must</em> be a map from string to: Integer,Long,String,String[] */
	Future<?> post(String url, Map<String, Object> map, IResponseCallback callback);

	Future<?> makeRoot(String url, IResponseCallback callback);

	void shutdown();

	public static class Utils {

		public static boolean debug = false;

//		public static IRepositoryFacard defaultFacard() {
//			return new RepositoryFacard(IHttpClient.Utils.defaultClient(), "sfm");
//		}
//
//		public static IRepositoryFacard defaultFacardForCardExplorer() {
//			return new RepositoryFacard(IHttpClient.Utils.defaultClient(), "1.json");
//		}
//
//		public static IRepositoryFacard defaultFacardWithHeaders(String name, String value) {
//			return new RepositoryFacard(IHttpClient.Utils.defaultClient().setDefaultHeaders(Arrays.<NameValuePair> asList(new BasicNameValuePair(name, value))), "sfm");
//		}
//
//		public static IRepositoryFacard frontEnd(String host, int port, String userName, String password) {
//			return new RepositoryFacard(IHttpClient.Utils.builder(host, port).withCredentials(userName, password), "sfm");
//		}

	}



}