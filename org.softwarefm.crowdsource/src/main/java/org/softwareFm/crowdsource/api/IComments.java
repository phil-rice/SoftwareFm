package org.softwareFm.crowdsource.api;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;

public interface IComments extends ICommentsReader {

	void addComment(String softwareFmId, String userkey, ICommentDefn call, String text);

	public static class Utils {
		public static void addComment(IContainer api, final String softwareFmId, final String userkey, final ICommentDefn call, final String text) {
			api.accessComments(new ICallback<IComments>() {
				@Override
				public void process(IComments comments) throws Exception {
					comments.addComment(softwareFmId, userkey, call, text);
				}
			}).get();
		}
	}
}
