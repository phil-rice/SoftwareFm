/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.comments.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.ApiTest;
import org.softwareFm.crowdsource.api.ICommentsReader;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public abstract class AbstractCommentsReaderTest extends ApiTest {

	protected final long time1 = 1000l;
	protected final long time2 = 2000l;
	protected final Map<String, Object> comment1 = Maps.stringObjectMap(CommentConstants.timeKey, time1, LoginConstants.softwareFmIdKey, "someNewSoftwareFmId0", CommentConstants.creatorKey, "moniker", CommentConstants.textKey, "text1");
	protected final Map<String, Object> comment2 = Maps.stringObjectMap(CommentConstants.timeKey, time2, LoginConstants.softwareFmIdKey, "someNewSoftwareFmId0", CommentConstants.creatorKey, "moniker", CommentConstants.textKey, "text2");
	protected String softwareFmId;

	abstract protected ICrowdSourcedApi getApi();

	@SuppressWarnings("unchecked")
	public void testGroupComments() {
		remoteOperations.init("a");
		ensureUserHasDefaultValues();
		final Map<String, String> groupIdToCrypto = setUpGroups("groupId1", "groupId2", "groupId3");

		IUserAndGroupsContainer container = getApi().makeUserAndGroupsContainer();
		container.accessGroupReader(new IFunction1<IGroupsReader, Void>() {
			@Override
			public Void apply(IGroupsReader groups) throws Exception {
				for (String groupId : groupIdToCrypto.keySet()) {
					String groupCrypto = groupIdToCrypto.get(groupId);
					String groupCommentCrypto = groups.getGroupProperty(groupId, groupCrypto, CommentConstants.commentCryptoKey);

					IFileDescription fd = IFileDescription.Utils.encrypted("a/b", groupId + "." + CommentConstants.commentExtension, groupCommentCrypto);
					remoteOperations.append(fd, Maps.with(comment1, "a", groupId));
					remoteOperations.append(fd, comment2);
					remoteOperations.addAllAndCommit("a", "testGlobal");
				}
				return null;
			}

		}, ICallback.Utils.<Void> noCallback()).get();
		IGitReader.Utils.clearCache(container);
		container.accessCommentsReader(new IFunction1<ICommentsReader, Void>() {
			@Override
			public Void apply(ICommentsReader reader) throws Exception {
				String sourcekey = CommentConstants.sourceKey;
				List<Map<String, Object>> actual = reader.groupComments("a/b", softwareFmId, userKey0);
				assertEquals(Arrays.asList(//
						Maps.with(comment1, "a", "groupId1", sourcekey, "groupId1Name"), Maps.with(comment2, sourcekey, "groupId1Name"),//
						Maps.with(comment1, "a", "groupId2", sourcekey, "groupId2Name"), Maps.with(comment2, sourcekey, "groupId2Name"),//
						Maps.with(comment1, "a", "groupId3", sourcekey, "groupId3Name"), Maps.with(comment2, sourcekey, "groupId3Name")), actual);
				return null;
			}
		}, ICallback.Utils.<Void> noCallback()).get();
	}

	private void ensureUserHasDefaultValues() {
		IUserAndGroupsContainer container = getApi().makeUserAndGroupsContainer();
		container.accessUserReader(new IFunction1<IUserReader, Void>() {
			@Override
			public Void apply(IUserReader user) throws Exception {
				user.getUserProperty(softwareFmId, userKey0, GroupConstants.groupCryptoKey);
				user.getUserProperty(softwareFmId, userKey0, GroupConstants.membershipCryptoKey);
				return null;
			}
		}, ICallback.Utils.<Void> noCallback()).get();
	}

	@SuppressWarnings("unchecked")
	public void testGlobal() {
		IFileDescription fd = IFileDescription.Utils.plain("a/b", CommentConstants.globalCommentsFile);
		remoteOperations.init("a");
		remoteOperations.append(fd, comment1);
		remoteOperations.append(fd, comment2);
		remoteOperations.addAllAndCommit("a", "testGlobal");
		IContainer container = getApi().makeContainer();
		container.accessCommentsReader(new IFunction1<ICommentsReader, Void>() {
			@Override
			public Void apply(ICommentsReader reader) throws Exception {
				assertEquals(addSource("asd", comment1, comment2), reader.globalComments("a/b", "asd"));
				return null;
			}
		}, ICallback.Utils.<Void> noCallback()).get();
	}

	@SuppressWarnings("unchecked")
	public void testUserComments() {
		ensureUserHasDefaultValues();
		String userCommentsCrypto = getUserCommentCrypto();
		IFileDescription fd = IFileDescription.Utils.encrypted("a/b", softwareFmId + "." + CommentConstants.commentExtension, userCommentsCrypto);
		remoteOperations.init("a");
		remoteOperations.append(fd, comment1);
		remoteOperations.append(fd, comment2);
		remoteOperations.addAllAndCommit("a", "testGlobal");
		IContainer container = getApi().makeContainer();
		container.accessCommentsReader(new IFunction1<ICommentsReader, Void>() {
			@Override
			public Void apply(ICommentsReader reader) throws Exception {
				assertEquals(addSource("asd", comment1, comment2), reader.myComments("a/b", softwareFmId, userKey0, "asd"));
				return null;
			}
		}, ICallback.Utils.<Void> noCallback()).get();
	}

	public void testUserCommentsWhenNoCommentsHaveBeenMade() {
		remoteOperations.init("a");
		remoteOperations.put(IFileDescription.Utils.plain("a/z"), v11);
		remoteOperations.addAllAndCommit("a", "testUserCommentsWhenNoCommentsHaveBeenMade");// needed to ensure that the repository is in a good state
		IContainer container = getApi().makeContainer();
		container.accessCommentsReader(new IFunction1<ICommentsReader, Void>() {
			@Override
			public Void apply(ICommentsReader reader) throws Exception {
				assertEquals(Collections.emptyList(), reader.myComments("a/b", softwareFmId, userKey0, "asd"));
				return null;
			}
		}, ICallback.Utils.<Void> noCallback());
	}

	protected Map<String, String> setUpGroups(final String... groupIds) {
		final Map<String, String> groupIdToCrypto = Maps.newMap();
		IUserAndGroupsContainer container = getServerUserAndGroupsContainer();
		container.accessUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership userMembership) throws Exception {
				for (String groupId : groupIds) {
					String groupCrypto = Crypto.makeKey();
					groups.setGroupProperty(groupId, groupCrypto, GroupConstants.groupNameKey, groupId + "Name");
					String groupCommentCrypto = Crypto.makeKey();
					groupIdToCrypto.put(groupId, groupCrypto);
					groups.setGroupProperty(groupId, groupCrypto, CommentConstants.commentCryptoKey, groupCommentCrypto);
					userMembership.addMembership(softwareFmId, userKey0, groupId, groupCrypto, GroupConstants.invitedStatus);
				}
			}
		}).get();
		return groupIdToCrypto;
	}

	private String getUserCommentCrypto() {
		IUserAndGroupsContainer container = getApi().makeUserAndGroupsContainer();
		String userCommentsCrypto = container.accessUserReader(new IFunction1<IUserReader, String>() {
			@Override
			public String apply(IUserReader from) throws Exception {
				return from.getUserProperty(softwareFmId, userKey0, CommentConstants.commentCryptoKey);
			}
		}, ICallback.Utils.<String> noCallback()).get();
		return userCommentsCrypto;
	}

	protected List<Map<String, Object>> addSource(String source, Map<String, Object>... rawExpected) {
		List<Map<String, Object>> expected = Lists.map(Arrays.asList(rawExpected), Maps.<String, Object> withFn(CommentConstants.sourceKey, source));
		return expected;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		truncateUsersTable();
		softwareFmId = createUser();
	}
}