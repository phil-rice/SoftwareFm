package org.softwareFm.swt.comments.internal;

import java.util.concurrent.TimeUnit;

import org.softwareFm.client.http.api.IHttpClient;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.maps.IHasUrlCache;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.comments.ICommentDefn;
import org.softwareFm.eclipse.constants.CommentConstants;
import org.softwareFm.swt.comments.ICommentWriter;

public final class CommentWriter implements ICommentWriter {
	private final IHttpClient client;
	private final long timeoutMs;
	private final IHasUrlCache cache;

	public CommentWriter(IHttpClient client, long timeoutMs, IHasUrlCache cache) {
		this.client = client;
		this.timeoutMs = timeoutMs;
		this.cache = cache;
	}

	@Override
	public void addComment(String softwareFmId, String userCrypto, ICommentDefn defn, String text) {
		try {
			IFileDescription fileDescription = defn.fileDescription();
			String encodedText = Crypto.aesEncrypt(userCrypto, text);
			client.post(Urls.compose(CommentConstants.commentCommandPrefix, CommentConstants.addCommandSuffix, fileDescription.url())).//
					addParam(CommentConstants.filenameKey, defn.fileDescription().name()).//
					addParam(LoginConstants.softwareFmIdKey, softwareFmId).//
					addParam(CommentConstants.textKey, encodedText).//
					execute(IResponseCallback.Utils.throwExeceptionIfFailCallback()).get(timeoutMs, TimeUnit.MILLISECONDS);
			cache.clearCache(fileDescription.url());
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}

	}
}