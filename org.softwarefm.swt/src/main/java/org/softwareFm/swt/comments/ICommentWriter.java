package org.softwareFm.swt.comments;

import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.common.maps.IHasUrlCache;
import org.softwareFm.eclipse.comments.ICommentDefn;
import org.softwareFm.swt.comments.internal.CommentWriter;

public interface ICommentWriter {

	void addComment(String softwareFmId, String userCrypto, ICommentDefn defn, String text);

	public static class Utils {
		public static ICommentWriter commentWriter(IHttpClient client, long timeoutMs, IHasUrlCache cache) {
			return new CommentWriter(client, timeoutMs, cache);
		}

		public static ICommentWriter sysoutCommentsWriter() {
			return new ICommentWriter() {
				@Override
				public void addComment(String softwareFmId, String userCrypto, ICommentDefn defn, String text) {
					System.out.println("SoftwareFmID: " + softwareFmId + ", crypto: " + userCrypto + ", Defn: " + defn + ", Text: " + text);
				}
			};
		}

		public static ICommentWriter exceptionCommentWriter() {
			return new ICommentWriter() {
				@Override
				public void addComment(String softwareFmId, String userCrypto, ICommentDefn defn, String text) {
					throw new UnsupportedOperationException();
				}
			};
		}
	}
}
