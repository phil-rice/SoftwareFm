/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.comments.internal;

import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.api.ICommentDefn;
import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.runnable.Callables;

public class CommentsForServer extends AbstractCommentsReader implements IComments {

	private final Callable<Long> timeGetter;

	public CommentsForServer(IUserAndGroupsContainer api, Callable<Long> timeGetter) {
		super(api);
		this.timeGetter = timeGetter;
	}

	@Override
	public void addComment(String softwareFmId, String userCrypto, ICommentDefn defn, String text) {
		Map<String, Object> data = makeData(softwareFmId, userCrypto, text);
		IFileDescription fileDescription = defn.fileDescription();
		container.gitOperations().append(fileDescription, data);
	}

	private Map<String, Object> makeData(final String softwareFmId, final String userCrypto, final String text) {
		return container.accessUserReader(new IFunction1<IUserReader, Map<String, Object>>() {
			@Override
			public Map<String, Object> apply(IUserReader user) throws Exception {
				return Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId, //
						CommentConstants.textKey, text, //
						CommentConstants.creatorKey, user.getUserProperty(softwareFmId, userCrypto, LoginConstants.monikerKey),//
						CommentConstants.timeKey, Callables.call(timeGetter));
			}
		}, ICallback.Utils.<Map<String, Object>> noCallback()).get();
	}

}