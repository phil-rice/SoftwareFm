/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.mavenExtractor;

import java.util.Map;
import java.util.Map.Entry;

import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.httpClient.response.IResponse;
import org.softwareFm.repositoryFacard.IRepositoryFacard;
import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.utilities.exceptions.WrappedException;

public class SlingWalker {

	private final int targetDepth;

	public SlingWalker(int targetDepth) {
		this.targetDepth = targetDepth;

	}

	public void walk(String url, ISlingVisitor visitor) {
		IHttpClient client = IHttpClient.Utils.defaultClient();
		IRepositoryFacard repository = IRepositoryFacard.Utils.defaultFacardForCardExplorer();
		try {
			walk(repository, client, url, 0, visitor);
		} finally {
			repository.shutdown();
			client.shutdown();
		}
	}

	private void walk(final IRepositoryFacard repository, final IHttpClient client, final String url, final int depth, final ISlingVisitor visitor) {
		try {
			if (depth >= targetDepth) {
				visitor.visit(repository, client, url);
				return;
			}
			repository.get(url, new IRepositoryFacardCallback() {
				public void process(IResponse response, Map<String, Object> data) throws Exception {
					if (data == null)
						System.out.println("url: " + url + " was null");
					else
						for (Entry<String, Object> entry : data.entrySet())
							if (entry.getValue() instanceof Map)
								try {
									walk(repository, client, url + "/" + entry.getKey(), depth + 1, visitor);
								} catch (Exception e) {
									visitor.exception(url,e);
								}

				}
			}).get();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

	public static void main(String[] args) {
		new SlingWalker(2).walk("/softwareFm", ISlingVisitor.Utils.sysout);
	}
}