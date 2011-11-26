/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.mavenExtractor;

import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.repositoryFacard.IRepositoryFacard;

public interface ISlingVisitor {

	void visit(IRepositoryFacard repository, IHttpClient client, String url) throws Exception;

	void exception(String url, Exception e);
	public static class Utils {
		public final static ISlingVisitor sysout = new ISlingVisitor() {
			private int count;

			public void visit(IRepositoryFacard repository, IHttpClient client, String url) {
				System.out.println(count++ + url);
			}

			public void exception(String url, Exception e) {
				e.printStackTrace();
			}
		};
	}


}