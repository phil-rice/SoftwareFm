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
