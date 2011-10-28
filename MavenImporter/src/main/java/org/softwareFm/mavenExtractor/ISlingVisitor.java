package org.softwareFm.mavenExtractor;

import org.softwareFm.httpClient.api.IHttpClient;
import org.softwareFm.repositoryFacard.IRepositoryFacard;

public interface ISlingVisitor {

	void visit(IRepositoryFacard repository, IHttpClient client, String url) throws Exception;

	public static class Utils {
		public final static ISlingVisitor sysout = new ISlingVisitor() {
			private int count;

			public void visit(IRepositoryFacard repository, IHttpClient client, String url) {
				System.out.println(count++ + url);
			}
		};
	}

}
