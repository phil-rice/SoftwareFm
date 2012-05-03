package org.softwareFm.crowdsource.api.newGit.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.crowdsource.api.UserData;
import org.softwareFm.crowdsource.api.git.GitTest;
import org.softwareFm.crowdsource.api.newGit.IAccessControlList;
import org.softwareFm.crowdsource.api.newGit.IRepoDataFactory;
import org.softwareFm.crowdsource.api.newGit.IRepoLocator;
import org.softwareFm.crowdsource.api.newGit.IRepoReader;
import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.api.newGit.ISources;
import org.softwareFm.crowdsource.api.newGit.RepoLocation;
import org.softwareFm.crowdsource.api.newGit.SourcedMap;
import org.softwareFm.crowdsource.api.newGit.facard.IGitFacard;
import org.softwareFm.crowdsource.api.newGit.facard.ILinkedGitFacard;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.collections.ITransactionalMutableSimpleSet;
import org.softwareFm.crowdsource.utilities.collections.Iterables;
import org.softwareFm.crowdsource.utilities.collections.Sets;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.json.Json;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.maps.TransactionalSimpleSet;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.crowdsource.utilities.url.Urls;

abstract public class RepoTest extends GitTest {

	// Note for speed reasons, the transaction manager is not remade at the start of each test. Hence it is a static
	// It may be remade by teardown if the current jobs don't exit. Not perfect but notable time difference!
	protected ITransactionManager transactionManager = ITransactionManager.Utils.standard(CommonConstants.threadPoolSizeForTests, CommonConstants.testTimeOutMs);

	protected static final IUrlGenerator userUrlGenerator = LoginConstants.userGenerator("data");
	protected static final IUrlGenerator groupUrlGenerator = GroupConstants.groupsGenerator("data");

	protected static final String userId1 = "uId1";
	protected static final String userId2 = "uId2";
	protected static final String userId3 = "uId3";

	protected static final String userCrypto1 = Crypto.makeKey();
	protected static final String userCrypto2 = Crypto.makeKey();
	protected static final String userCrypto3 = Crypto.makeKey();

	protected static final String userMembershipCrypto1 = Crypto.makeKey();
	protected static final String userMembershipCrypto2 = Crypto.makeKey();
	protected static final String userMembershipCrypto3 = Crypto.makeKey();

	protected static final String userCommentCrypto1 = Crypto.makeKey();
	protected static final String userCommentCrypto2 = Crypto.makeKey();
	protected static final String userCommentCrypto3 = Crypto.makeKey();

	protected static final String groupId1 = "gId1";
	protected static final String groupId2 = "gId2";
	protected static final String groupId3 = "gId3";

	protected static final String groupCrypto1 = Crypto.makeKey();
	protected static final String groupCrypto2 = Crypto.makeKey();
	protected static final String groupCrypto3 = Crypto.makeKey();

	protected static final String groupCommentCrypto1 = Crypto.makeKey();
	protected static final String groupCommentCrypto2 = Crypto.makeKey();
	protected static final String groupCommentCrypto3 = Crypto.makeKey();

	protected static final String groupMembershipCrypto1 = Crypto.makeKey();
	protected static final String groupMembershipCrypto2 = Crypto.makeKey();
	protected static final String groupMembershipCrypto3 = Crypto.makeKey();

	protected static final String miscCrypto1 = Crypto.makeKey();
	protected static final String miscCrypto2 = Crypto.makeKey();
	protected static final String miscCrypto3 = Crypto.makeKey();

	protected static final Map<String, Object> membershipMapForGroup1Admin = makeMembershipMap(groupId1, groupMembershipCrypto1, groupCommentCrypto1, GroupConstants.adminStatus);
	protected static final Map<String, Object> membershipMapForGroup1Member = makeMembershipMap(groupId1, groupMembershipCrypto1, groupCommentCrypto1, GroupConstants.memberStatus);
	protected static final Map<String, Object> membershipMapForGroup1Invited = makeMembershipMap(groupId1, groupMembershipCrypto1, groupCommentCrypto1, GroupConstants.invitedStatus);

	protected static final Map<String, Object> membershipMapForGroup2Admin = makeMembershipMap(groupId2, groupMembershipCrypto2, groupCommentCrypto2, GroupConstants.adminStatus);
	protected static final Map<String, Object> membershipMapForGroup2Member = makeMembershipMap(groupId2, groupMembershipCrypto2, groupCommentCrypto2, GroupConstants.memberStatus);
	protected static final Map<String, Object> membershipMapForGroup2Invited = makeMembershipMap(groupId2, groupMembershipCrypto2, groupCommentCrypto2, GroupConstants.invitedStatus);

	protected static final Map<String, Object> membershipMapForGroup3Admin = makeMembershipMap(groupId3, groupMembershipCrypto3, groupCommentCrypto3, GroupConstants.adminStatus);
	protected static final Map<String, Object> membershipMapForGroup3Member = makeMembershipMap(groupId3, groupMembershipCrypto3, groupCommentCrypto3, GroupConstants.memberStatus);
	protected static final Map<String, Object> membershipMapForGroup3Invited = makeMembershipMap(groupId3, groupMembershipCrypto3, groupCommentCrypto3, GroupConstants.invitedStatus);

	protected static final Map<String, Object> v11User1 = Maps.with(v11, LoginConstants.softwareFmIdKey, userId1);
	protected static final Map<String, Object> v12User1 = Maps.with(v12, LoginConstants.softwareFmIdKey, userId1);
	protected static final Map<String, Object> v11User2 = Maps.with(v11, LoginConstants.softwareFmIdKey, userId2);
	protected static final Map<String, Object> v12User2 = Maps.with(v12, LoginConstants.softwareFmIdKey, userId2);

	protected static final UserData user1Data = new UserData("someEmail", userId1, userCrypto1);
	protected static final UserData user2Data = new UserData("someEmail", userId2, userCrypto2);

	protected ILinkedGitFacard localFacard;
	protected IGitFacard remoteFacard;
	protected RepoData repoData;

	protected Set<String> hasPulledSetRaw = Sets.newSet();
	protected AtomicInteger hasPulledCommitCount = new AtomicInteger();
	protected AtomicInteger hasPulledRollbackCount = new AtomicInteger();
	protected ITransactionalMutableSimpleSet<String> hasPulled = new TransactionalSimpleSet<String>(hasPulledSetRaw) {
		@Override
		public void commit() {
			hasPulledCommitCount.incrementAndGet();
		}

		@Override
		public void rollback() {
			hasPulledRollbackCount.incrementAndGet();
		}
	};

	protected IRepoLocator repoLocator;

	protected void initRepos(IGitFacard gitFacard, String... rls) {
		for (String rl : rls)
			gitFacard.init(rl);
	}

	protected void addAllAndCommit(IGitFacard gitFacard, String... rls) {
		for (String rl : rls)
			gitFacard.commit(gitFacard.addAll(rl), "someCommitMessage");
	}

	/** Note doesnt add or commit it, as that is best done only once. You need to have inited the repository before this will work */
	protected void createArbitaryFileForUser(IGitFacard gitFacard, String userId, String userCrypto, Object... namesAndAttributes) {
		Map<String, Object> map = Maps.<String, Object> makeMapWithoutNullValues(namesAndAttributes);
		String userProperiesAsEncryptedString = Crypto.aesEncrypt(userCrypto, Json.toString(map));
		String userRl = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, userId));
		String userDataRl = Urls.compose(userRl, CommonConstants.dataFileName);
		gitFacard.putFileReturningRepoRl(userDataRl, userProperiesAsEncryptedString);
	}

	/** Note doesnt add or commit it, as that is best done only once. You need to have inited the repository before this will work */
	protected void createFileForUser(IGitFacard gitFacard, String userId, String userCrypto, String membershipCrypto, String commentCrypto) {
		createArbitaryFileForUser(gitFacard, userId, userCrypto, CommentConstants.commentCryptoKey, commentCrypto, GroupConstants.membershipCryptoKey, membershipCrypto);
	}

	protected void createFileForUser1(IGitFacard gitFacard) {
		createFileForUser(gitFacard, userId1, userCrypto1, userMembershipCrypto1, userCommentCrypto1);
	}

	protected void createMembershipFileForUser(IGitFacard gitFacard, String userId, String membershipCrypto, Map<String, Object>... membershipData) {
		String text = Strings.join(Iterables.mapValues(Json.toStringAndEncryptFn(membershipCrypto), membershipData), "\n");
		String userRl = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, userId));
		String membershipRl = Urls.compose(userRl, GroupConstants.membershipFileName);
		gitFacard.putFileReturningRepoRl(membershipRl, text);
	}

	static protected Map<String, Object> makeMembershipMap(String groupId, String membershipCrypto, String commentCrypto, String status) {
		return Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.membershipCryptoKey, membershipCrypto, CommentConstants.commentCryptoKey, commentCrypto, GroupConstants.membershipStatusKey, status);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		remoteFacard = IGitFacard.Utils.makeServerGitFacard(remoteRoot, getAccessControl());
		localFacard = IGitFacard.Utils.makeLocalGitFacard(localRoot, remoteRoot.getCanonicalPath(), getAccessControl());
		repoLocator = new IRepoLocator() {
			@Override
			public RepoLocation findRepository(ISingleSource source) {
				RepoLocation raw = remoteFacard.findRepoRl(source.fullRl());
				return RepoLocation.remote(localRoot, raw.rl);
			}
		};
		newRepo();
	}

	protected IAccessControlList getAccessControl() {
		return IAccessControlList.Utils.noAccessControl();
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			boolean closed = ITransactionManager.Utils.waitUntilNoActiveJobs(transactionManager, CommonConstants.testTimeOutMs);
			if (!closed) {
				transactionManager.shutdownAndAwaitTermination(CommonConstants.testTimeOutMs, TimeUnit.SECONDS);
				System.out.println("CAnnot close down transaction manager: " + transactionManager);
				transactionManager = ITransactionManager.Utils.standard(CommonConstants.threadPoolSizeForTests, CommonConstants.testTimeOutMs);
				transactionManagerChanged();
			}
			repoData.rollback();
		} finally {
			super.tearDown();
		}
	}

	protected void transactionManagerChanged() {
	}

	protected void putFile(IGitFacard gitFacard, String rl, String crypto, Map<String, Object>... maps) {
		putFile(gitFacard, rl, crypto, Arrays.asList(maps));
	}

	protected void putFile(IGitFacard gitFacard, String rl, String crypto, List<Map<String, Object>> lines) {
		Iterable<String> encodedList = Iterables.map(lines, Json.toStringAndEncryptFn(crypto));
		gitFacard.putFileReturningRepoRl(rl, Strings.join(encodedList, "\n"));
	}

	protected RepoData newRepo() {
		repoData = (RepoData) IRepoDataFactory.Utils.localFactory(localFacard, repoLocator, hasPulled).build();
		return repoData;
	}

	protected void checkRead(ISources sources, int index, SourcedMap... expected) {
		Iterable<SourcedMap> actual = IRepoReader.Utils.read(repoData, sources, index);
		assertEquals(Arrays.asList(expected), Iterables.list(actual));
	}

	protected void checkRead(ISingleSource source, int index, SourcedMap... expected) {
		Iterable<SourcedMap> actual = IRepoReader.Utils.read(repoData, source, index);
		assertEquals(Arrays.asList(expected), Iterables.list(actual));
	}

}
