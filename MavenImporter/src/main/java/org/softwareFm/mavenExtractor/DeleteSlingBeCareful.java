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
		new SlingWalker(3).walk("/softwareFm/data", new DeleteSlingBeCareful());
	}
}
