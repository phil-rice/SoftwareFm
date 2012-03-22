package org.softwareFm.crowdsource.api;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;

public interface IComments extends ICommentsReader {

	void addComment(String softwareFmId, String userkey, ICommentDefn call, String text);

	public static class Utils {
		public static void addComment(ICrowdSourcedReadWriteApi api, final String softwareFmId, final String userkey, final ICommentDefn call, final String text) {
			api.modifyComments(new ICallback<IComments>() {
				@Override
				public void process(IComments comments) throws Exception {
					comments.addComment(softwareFmId, userkey, call, text);
				}
			});
		}
	}
}
