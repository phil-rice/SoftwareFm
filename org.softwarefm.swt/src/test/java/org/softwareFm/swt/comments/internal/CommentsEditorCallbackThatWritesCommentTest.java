package org.softwareFm.swt.comments.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.Test;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.runnable.Runnables;
import org.softwareFm.swt.comments.ICommentWriter;

public class CommentsEditorCallbackThatWritesCommentTest extends TestCase {

	private Runnable whenFinished;
	private IUserReader userReader;
	private IGroupsReader groupsReader;
	private ICommentWriter commentWriter;
	private final String softwareFmId = "sfmId1";
	private final String userCrypto = Crypto.makeKey();
	private final String url = "url";
	private final String text = "text";
	private final String groupId = "groupId1";
	private final String groupCrypto = Crypto.makeKey();

	@SuppressWarnings("unchecked")
	private final List<Map<String, Object>> groupsData = Arrays.asList(//
			Maps.stringObjectMap(GroupConstants.groupIdKey, "groupIdBefore", GroupConstants.groupCryptoKey, Crypto.makeKey()),//
			Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCrypto), //
			Maps.stringObjectMap(GroupConstants.groupIdKey, "groupIdAfter", GroupConstants.groupCryptoKey, Crypto.makeKey()));
	private CommentsEditorCallbackThatWritesComment callback;

	@Test
	public void testCancel() {
		whenFinished.run();
		EasyMock.replay(userReader, groupsReader, commentWriter, whenFinished);

		callback.cancel();
	}

	public void testEveryone() {
		whenFinished.run();
		commentWriter.addComment(softwareFmId, userCrypto, ICommentDefn.Utils.everyoneInitial(url), text);
		EasyMock.replay(userReader, groupsReader, commentWriter, whenFinished);

		callback.everyoneComment(url, text);
	}

	public void testMy() {
		whenFinished.run();
		EasyMock.expect(userReader.getUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey)).andReturn(Crypto.makeKey()).times(2);
		EasyMock.replay(userReader);

		commentWriter.addComment(softwareFmId, userCrypto, ICommentDefn.Utils.myInitial(userReader, softwareFmId, userCrypto, url), text);
		EasyMock.replay(groupsReader, commentWriter, whenFinished);

		callback.youComment(url, text);
	}

	public void testGroup() {
		whenFinished.run();
		String groupCommentCrypto = Crypto.makeKey();
		EasyMock.expect(groupsReader.getGroupProperty(groupId, groupCrypto, CommentConstants.commentCryptoKey)).andReturn(groupCommentCrypto).times(2);
		EasyMock.replay(groupsReader);

		commentWriter.addComment(softwareFmId, userCrypto, ICommentDefn.Utils.groupInitial(groupsReader, groupId, groupCrypto, url), text);
		EasyMock.replay(userReader, commentWriter, whenFinished);

		callback.groupComment(url, 1, text);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		whenFinished = Runnables.count();
		userReader = EasyMock.createMock(IUserReader.class);
		groupsReader = EasyMock.createMock(IGroupsReader.class);
		commentWriter = EasyMock.createMock(ICommentWriter.class);
		whenFinished = EasyMock.createMock(Runnable.class);
		callback = new CommentsEditorCallbackThatWritesComment(userReader, groupsReader, commentWriter, softwareFmId, userCrypto, groupsData, whenFinished);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		EasyMock.verify(userReader, groupsReader, commentWriter, whenFinished);
	}
}
