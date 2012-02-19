package org.softwareFm.swt.comments;

import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.eclipse.comments.ICommentDefn;
import org.softwareFm.swt.comments.internal.CommentWriter;

public interface ICommentWriter {

	void addComment(String softwareFmId,String userCrypto, ICommentDefn defn, String text);

	public static class Utils {
		public static ICommentWriter commentWriter(IHttpClient client, long timeoutMs) {
			return new CommentWriter(client, timeoutMs);
		}
	}
}
