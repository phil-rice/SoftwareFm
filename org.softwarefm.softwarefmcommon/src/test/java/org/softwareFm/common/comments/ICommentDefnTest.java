package org.softwareFm.common.comments;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.eclipse.comments.ICommentDefn;
import org.softwareFm.eclipse.comments.internal.CommentDefn;
import org.softwareFm.eclipse.constants.CommentConstants;

public class ICommentDefnTest extends TestCase {
	String url = "someUrl";
	int replyIndex = 5;
	String softwareFmId = "sfmId";
	String groupId = "groupId";
	String crypto = Crypto.makeKey();
	private IUserReader userReader;
	private IGroupsReader groupsReader;

	public void testGlobal() {
		replayMocks();

		assertEquals(initial(CommentConstants.globalCommentsFile, null), ICommentDefn.Utils.everyoneInitial(url));
		assertEquals(reply(CommentConstants.globalCommentsFile, null), ICommentDefn.Utils.everyoneReply(url, replyIndex));
	}

	public void testMy() {
		String userCommentCrypto = Crypto.makeKey();
		EasyMock.expect(userReader.getUserProperty(softwareFmId, crypto, CommentConstants.commentCryptoKey)).andReturn(userCommentCrypto).times(2);
		replayMocks();
		assertEquals(initial(softwareFmId, userCommentCrypto), ICommentDefn.Utils.myInitial(userReader, softwareFmId, crypto, url));
		assertEquals(reply(softwareFmId, userCommentCrypto), ICommentDefn.Utils.myReply(userReader, softwareFmId, crypto, url, replyIndex));
	}

	public void testGroup() {
		String groupCommentCrypto = Crypto.makeKey();
		EasyMock.expect(groupsReader.getGroupProperty(groupId, crypto, CommentConstants.commentCryptoKey)).andReturn(groupCommentCrypto).times(2);
		replayMocks();
		assertEquals(initial(groupId, groupCommentCrypto), ICommentDefn.Utils.groupInitial(groupsReader, groupId, crypto, url));
		assertEquals(reply(groupId, groupCommentCrypto), ICommentDefn.Utils.groupReply( groupsReader, groupId, crypto, url, replyIndex));
	}

	protected void replayMocks() {
		EasyMock.replay(userReader, groupsReader);
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
		userReader = EasyMock.createMock(IUserReader.class);
		groupsReader = EasyMock.createMock(IGroupsReader.class);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		EasyMock.verify(userReader, groupsReader);
	}
}
