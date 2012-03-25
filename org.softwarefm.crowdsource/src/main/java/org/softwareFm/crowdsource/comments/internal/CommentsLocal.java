package org.softwareFm.crowdsource.comments.internal;

import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsource.api.ICommentDefn;
import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.httpClient.IHttpClient;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.maps.IHasUrlCache;
import org.softwareFm.crowdsource.utilities.url.Urls;

public class CommentsLocal extends AbstractCommentsReader implements IComments {

	private final long timeOutMs;
	private final IHasUrlCache urlCache;

	public CommentsLocal(IContainer api, IHasUrlCache urlCache, long timeOutMs) {
		super(api);
		this.urlCache = urlCache;
		this.timeOutMs = timeOutMs;
	}

	@Override
	public void addComment(final String softwareFmId, final String userCrypto, final ICommentDefn defn, final String text) {
		api.modify(IHttpClient.class, new ICallback<IHttpClient>() {
			@Override
			public void process(IHttpClient client) throws Exception {
				IFileDescription fileDescription = defn.fileDescription();
				String encodedText = Crypto.aesEncrypt(userCrypto, text);
				client.post(Urls.compose(CommentConstants.commentCommandPrefix, CommentConstants.addCommandSuffix, fileDescription.url())).//
						addParam(CommentConstants.filenameKey, defn.fileDescription().name()).//
						addParam(LoginConstants.softwareFmIdKey, softwareFmId).//
						addParam(CommentConstants.textKey, encodedText).//
						execute(IResponseCallback.Utils.throwExeceptionIfFailCallback()).get(timeOutMs, TimeUnit.MILLISECONDS);
				urlCache.clearCaches();
			}
		});
	}

}
