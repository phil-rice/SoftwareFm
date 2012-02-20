package org.softwareFm.server.processors;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.callbacks.ICallback3;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.exceptions.WrappedException;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.eclipse.comments.ICommentDefn;
import org.softwareFm.eclipse.constants.CommentConstants;
import org.softwareFm.eclipse.user.IUserMembershipReader;

public class CommentProcessorMethodTest extends TestCase {

	private final String userCrypto = Crypto.makeKey();
	private final String groupCrypto = Crypto.makeKey();
	private final String groupCommentCrypto = Crypto.makeKey();
	private final String userCommentCrypto = Crypto.makeKey();
	private final String url = "someUrl";
	private final String softwareFmId = "sfmId1";
	private final String groupId = "groupId1";
	private IUserReader userReader;
	private IUserMembershipReader userMembershipReader;
	private IGroupsReader groupsReader;

	public void testEveryOneInitial() {
		checkGetCommentDefn(ICallback3.Utils.<IUserReader, IUserMembershipReader, IGroupsReader> noCallback(), //
				new Callable<ICommentDefn>() {
					@Override
					public ICommentDefn call() throws Exception {
						return ICommentDefn.Utils.everyoneInitial(url);
					}
				}, LoginConstants.softwareFmIdKey, softwareFmId, CommentConstants.filenameKey, CommentConstants.globalCommentsFile);
	}

	public void testMyInitial() {
		checkGetCommentDefn(new ICallback3<IUserReader, IUserMembershipReader, IGroupsReader>() {
			@Override
			public void process(IUserReader userReader, IUserMembershipReader second, IGroupsReader third) throws Exception {
				EasyMock.expect(userReader.getUserProperty(softwareFmId, userCrypto, CommentConstants.commentCryptoKey)).andReturn(userCommentCrypto).times(3);
			}
		}, new Callable<ICommentDefn>() {
			@Override
			public ICommentDefn call() throws Exception {
				return ICommentDefn.Utils.myInitial(userReader, softwareFmId, userCrypto, url);
			}
		}, LoginConstants.softwareFmIdKey, softwareFmId, CommentConstants.filenameKey, softwareFmId);
	}

	public void testGroupInitial() {
		checkGetCommentDefn(new ICallback3<IUserReader, IUserMembershipReader, IGroupsReader>() {
			@SuppressWarnings("unchecked")
			@Override
			public void process(IUserReader first, IUserMembershipReader userMembershipReader, IGroupsReader groupsReader) throws Exception {
				EasyMock.expect(groupsReader.getGroupProperty(groupId, groupCrypto, CommentConstants.commentCryptoKey)).andReturn(groupCommentCrypto).times(2);
				EasyMock.expect(userMembershipReader.walkGroupsFor(softwareFmId, userCrypto)).andReturn(Arrays.asList(//
						Maps.stringObjectMap(GroupConstants.groupIdKey, "wrongId1", GroupConstants.groupCryptoKey, "noGoodCrypto1"),//
						Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCrypto),//
						Maps.stringObjectMap(GroupConstants.groupIdKey, "wrongId2", GroupConstants.groupCryptoKey, "noGoodCrypto2")));
			}
		}, new Callable<ICommentDefn>() {
			@Override
			public ICommentDefn call() throws Exception {
				return ICommentDefn.Utils.groupInitial(groupsReader, groupId, groupCrypto, url);
			}
		}, LoginConstants.softwareFmIdKey, softwareFmId, CommentConstants.filenameKey, groupId);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userReader = EasyMock.createMock(IUserReader.class);
		userMembershipReader = EasyMock.createMock(IUserMembershipReader.class);
		groupsReader = EasyMock.createMock(IGroupsReader.class);
	}

	@Override
	protected void tearDown() throws Exception {
		EasyMock.verify(userReader, userMembershipReader, groupsReader);
		super.tearDown();
	}

	protected void checkGetCommentDefn(ICallback3<IUserReader, IUserMembershipReader, IGroupsReader> populate, Callable<ICommentDefn> expectedGetter, Object... nameAndValues) {
		try {
			Map<String, Object> params = Maps.makeMap(nameAndValues);
			populate.process(userReader, userMembershipReader, groupsReader);
			EasyMock.replay(userReader, userMembershipReader, groupsReader);

			CommentProcessor commentProcessor = new CommentProcessor(userReader, userMembershipReader, groupsReader, null, null);
			ICommentDefn actual = commentProcessor.getAddCommentDefn(url, userCrypto, params);
			ICommentDefn expected = Callables.call(expectedGetter);
			assertEquals(expected, actual);
			EasyMock.verify(userReader, userMembershipReader, groupsReader);
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}
