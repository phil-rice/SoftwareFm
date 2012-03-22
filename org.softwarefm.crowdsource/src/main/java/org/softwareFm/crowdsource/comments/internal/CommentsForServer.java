package org.softwareFm.crowdsource.comments.internal;

import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.api.ICommentDefn;
import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.ICrowdSourcedReadWriteApi;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.runnable.Callables;

public class CommentsForServer extends AbstractCommentsReader implements IComments {

	private final Callable<Long> timeGetter;

	public CommentsForServer(ICrowdSourcedReadWriteApi api, Callable<Long> timeGetter) {
		super(api);
		this.timeGetter = timeGetter;
	}

	@Override
	public void addComment(String softwareFmId, String userCrypto, ICommentDefn defn, String text) {
		Map<String, Object> data = makeData(softwareFmId, userCrypto, text);
		IFileDescription fileDescription = defn.fileDescription();
		api.gitOperations().append(fileDescription, data);
	}

	private Map<String, Object> makeData(final String softwareFmId, final String userCrypto, final String text) {
		return api.accessUserReader(new IFunction1<IUserReader, Map<String,Object>>() {
			@Override
			public Map<String, Object> apply(IUserReader user) throws Exception {
				return Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId, //
						CommentConstants.textKey, text, //
						CommentConstants.creatorKey, user.getUserProperty(softwareFmId, userCrypto, LoginConstants.monikerKey),//
						CommentConstants.timeKey, Callables.call(timeGetter));
			}
		});
	}

}
