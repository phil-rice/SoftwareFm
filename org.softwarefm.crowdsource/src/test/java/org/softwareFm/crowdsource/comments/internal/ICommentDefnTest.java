/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.comments.internal;

import org.softwareFm.crowdsource.api.ICommentDefn;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.TemporaryFileTest;
import org.softwareFm.crowdsource.api.internal.CrowdSourcedServerApi;
import org.softwareFm.crowdsource.api.server.IMailer;
import org.softwareFm.crowdsource.api.server.SignUpResult;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;
import org.springframework.jdbc.core.JdbcTemplate;

public class ICommentDefnTest extends TemporaryFileTest {
	String url = "someUrl";
	int replyIndex = 5;
	String softwareFmId = "sfmId";
	String groupId = "groupId";
	private CrowdSourcedServerApi api;
	private IUserAndGroupsContainer container;

	public void testGlobal() {
		assertEquals(initial(CommentConstants.globalCommentsFile, null), ICommentDefn.Utils.everyoneInitial(url));
		assertEquals(reply(CommentConstants.globalCommentsFile, null), ICommentDefn.Utils.everyoneReply(url, replyIndex));
	}

	public void testMy() {
		SignUpResult signUpResult = api.getServerDoers().getSignUpChecker().signUp("a@email.com", "someMoniker", "someSalt", "pass", softwareFmId);
		final String userCrypto = signUpResult.crypto;
		assertNotNull(signUpResult.errorMessage, userCrypto);
		String userCommentCrypto = container.accessUserReader(new IFunction1<IUserReader, String>() {
			@Override
			public String apply(IUserReader userReader) throws Exception {
				return userReader.getUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey);
			}
		}, ICallback.Utils.<String> noCallback()).get();
		assertNotNull(userCommentCrypto);
		assertEquals(initial(softwareFmId, userCommentCrypto), ICommentDefn.Utils.myInitial(container, softwareFmId, userCrypto, url));
		assertEquals(reply(softwareFmId, userCommentCrypto), ICommentDefn.Utils.myReply(container, softwareFmId, userCrypto, url, replyIndex));
	}

	public void testGroup() {
		final String groupCrypto = Crypto.makeKey();
		final String groupCommentCrypto = Crypto.makeKey();
		api.makeUserAndGroupsContainer().accessGroups(new ICallback<IGroups>() {
			@Override
			public void process(IGroups groups) throws Exception {
				groups.setGroupProperty(groupId, groupCrypto, CommentConstants.commentCryptoKey, groupCommentCrypto);
			}
		}).get();
		assertEquals(initial(groupId, groupCommentCrypto), ICommentDefn.Utils.groupInitial(container, groupId, groupCrypto, url));
		assertEquals(reply(groupId, groupCommentCrypto), ICommentDefn.Utils.groupReply(container, groupId, groupCrypto, url, replyIndex));
	}

	protected CommentDefn initial(String name, String crpto) {
		if (!name.contains("."))
			name += "." + CommentConstants.commentExtension;
		return new CommentDefn(IFileDescription.Utils.encrypted(url, name, crpto), -1);
	}

	protected CommentDefn reply(String name, String crpto) {
		if (!name.contains("."))
			name += "." + CommentConstants.commentExtension;
		return new CommentDefn(IFileDescription.Utils.encrypted(url, name, crpto), replyIndex);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ServerConfig serverConfig = ServerConfig.serverConfigForTests(root, IMailer.Utils.noMailer());
		api = (CrowdSourcedServerApi) ICrowdSourcedApi.Utils.forServer(serverConfig, ITransactionManager.Utils.standard(2, CommonConstants.testTimeOutMs));
		container = api.makeUserAndGroupsContainer();
		new JdbcTemplate(serverConfig.dataSource).update("delete From users");
	}
}