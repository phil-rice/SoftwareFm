/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.callProcessor.internal;

import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.crowdsource.api.ICommentDefn;
import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.IUserCryptoAccess;
import org.softwareFm.crowdsource.api.server.AbstractCallProcessor;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public class CommentProcessor extends AbstractCallProcessor {

	private final IUserAndGroupsContainer container;
	private final IUserCryptoAccess cryptoAccess;

	public CommentProcessor(IUserAndGroupsContainer container, IUserCryptoAccess cryptoAccess) {
		super(CommonConstants.POST, CommentConstants.commentCommandPrefix);
		this.container = container;
		this.cryptoAccess = cryptoAccess;
	}

	@Override
	public IProcessResult execute(final String actualUrl, final Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.softwareFmIdKey, CommentConstants.textKey, CommentConstants.filenameKey);
		String command = Strings.firstSegment(actualUrl.substring(1), "/");
		if (command.equals(CommentConstants.addCommandSuffix)) {
			container.accessComments(new ICallback<IComments>() {
				@Override
				public void process(IComments comments) throws Exception {
					String url = actualUrl.substring(CommentConstants.addCommandSuffix.length() + 1);
					String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
					String userCrypto = cryptoAccess.getCryptoForUser(softwareFmId);
					if (userCrypto == null)
						throw new IllegalArgumentException(MessageFormat.format(CommentConstants.illegalSoftwareFmId, parameters));
					ICommentDefn defn = getAddCommentDefn(url, userCrypto, parameters);
					String encoded = (String) parameters.get(CommentConstants.textKey);
					String text = Crypto.aesDecrypt(userCrypto, encoded);
					comments.addComment(softwareFmId, userCrypto, defn, text);
				}
			}).get();
			return IProcessResult.Utils.processString("");
		}
		throw new IllegalArgumentException(actualUrl);
	}

	public ICommentDefn getAddCommentDefn(final String url, final String userCrypto, final Map<String, Object> parameters) {
		final String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
		String filename = (String) parameters.get(CommentConstants.filenameKey);
		final String fileStem = Files.noExtension(filename);
		if (filename.equals(CommentConstants.globalCommentsFile))
			return ICommentDefn.Utils.everyoneInitial(url);
		else if (fileStem.equals(softwareFmId)) {
			return container.accessUserReader(new IFunction1<IUserReader, ICommentDefn>() {
				@Override
				public ICommentDefn apply(IUserReader userReader) throws Exception {
					String commentCrypto = userReader.getUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey);
					if (commentCrypto == null)
						throw new NullPointerException();
					return ICommentDefn.Utils.myInitial(container, softwareFmId, userCrypto, url);
				}
			}, ICallback.Utils.<ICommentDefn> noCallback()).get();
		} else {
			return container.accessUserMembershipReader(new IFunction2<IGroupsReader, IUserMembershipReader, ICommentDefn>() {
				@Override
				public ICommentDefn apply(IGroupsReader groupsReader, IUserMembershipReader userMembershipReader) {
					for (Map<String, Object> map : userMembershipReader.walkGroupsFor(softwareFmId, userCrypto)) {
						if (fileStem.equals(map.get(GroupConstants.groupIdKey))) {
							String groupCrypto = (String) map.get(GroupConstants.groupCryptoKey);
							return ICommentDefn.Utils.groupInitial(container, fileStem, groupCrypto, url);
						}
					}
					throw new IllegalArgumentException(MessageFormat.format(CommentConstants.cannotWorkOutFileDescription, parameters));
				}
			}, ICallback.Utils.<ICommentDefn> noCallback()).get();
		}
	}
}