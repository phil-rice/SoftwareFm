/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.comments.internal;

import java.util.concurrent.TimeUnit;

import org.softwareFm.crowdsource.api.ICommentDefn;
import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
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

	private final IHasUrlCache urlCache;
	private final long timeOutMs;

	public CommentsLocal(IUserAndGroupsContainer container, IHasUrlCache urlCache, long timeOutMs) {
		super(container);
		this.urlCache = urlCache;
		this.timeOutMs = timeOutMs;
	}

	@Override
	public void addComment(final String softwareFmId, final String userCrypto, final ICommentDefn defn, final String text) {
		container.access(IHttpClient.class, new ICallback<IHttpClient>() {
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