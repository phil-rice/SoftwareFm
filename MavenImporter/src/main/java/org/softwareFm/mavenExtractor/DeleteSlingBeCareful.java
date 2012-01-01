/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.mavenExtractor;

import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.requests.IResponseCallback;
import org.softwareFm.repositoryFacard.IRepositoryFacard;

public class DeleteSlingBeCareful implements ISlingVisitor {

	private int count;

	public void visit(IRepositoryFacard repository, IHttpClient client, String url) throws Exception {
		client.delete(url).execute(IResponseCallback.Utils.sysoutStatusCallback()).get();
		System.out.println(count++);
	}

	public static void main(String[] args) {
		// new SlingWalker(1).walk("/softwareFm/data", new DeleteSlingBeCareful());
	}

	public void exception(String url, Exception e) {
		e.printStackTrace();
	}
}