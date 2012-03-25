package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.ApiTest;
import org.softwareFm.crowdsource.api.IComments;
import org.softwareFm.crowdsource.api.ICommentsReader;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.ICrowdSourcedReaderApi;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUser;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback.EnsureSameParameter;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback3;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;
import org.softwareFm.crowdsource.utilities.tests.Tests;

abstract public class AbstractCrowdReadWriterApiTest extends ApiTest {
	abstract protected ICrowdSourcedApi getApi();

	public void testGetSameReads() {
		ICrowdSourcedApi api = getApi();
		final ICrowdSourcedReaderApi reader = api.makeContainer();

		IFunction1<ICommentsReader, Integer> commentsFn = Functions.ensureSameParameters();
		reader.accessCommentsReader(commentsFn);
		reader.access(ICommentsReader.class, commentsFn);
		assertEquals(3, reader.accessCommentsReader(commentsFn).intValue());

		IFunction1<IUserReader, Integer> userReaderFn = Functions.ensureSameParameters();
		reader.accessUserReader(userReaderFn);
		reader.access(IUserReader.class, userReaderFn);
		assertEquals(3, reader.accessUserReader(userReaderFn).intValue());

		IFunction1<IGroupsReader, Integer> groupsReaderFn = Functions.ensureSameParameters();
		reader.accessGroupReader(groupsReaderFn);
		reader.access(IGroupsReader.class, groupsReaderFn);
		assertEquals(3, reader.accessGroupReader(groupsReaderFn).intValue());

		final IFunction2<IGroupsReader, IUserMembershipReader, Void> userMembershipReaderFn = Functions.ensureSameParameters2();
		reader.accessUserMembershipReader(userMembershipReaderFn);
		reader.access(IUserMembershipReader.class, new IFunction1<IUserMembershipReader, Void>() {
			@Override
			public Void apply(final IUserMembershipReader userMembershipReader) throws Exception {
				reader.access(IGroupsReader.class, new IFunction1<IGroupsReader, Void>() {
					@Override
					public Void apply(IGroupsReader groupsReader) throws Exception {
						return userMembershipReaderFn.apply(groupsReader, userMembershipReader);
					}
				});
				return null;
			}
		});
		reader.accessUserMembershipReader(userMembershipReaderFn);

		final IFunction1<IGitReader, Integer> gitReaderFn = Functions.ensureSameParameters();
		reader.accessGitReader(gitReaderFn);
		reader.access(IGitReader.class, gitReaderFn);
		assertEquals(3, reader.accessGitReader(gitReaderFn).intValue());

		reader.access(IGroupsReader.class, IUserMembershipReader.class, new IFunction2<IGroupsReader, IUserMembershipReader, Void>() {
			@Override
			public Void apply(IGroupsReader groupsReader, IUserMembershipReader userMembershipReader) throws Exception {
				return Functions.call(userMembershipReaderFn, groupsReader, userMembershipReader);
			}
		});

		reader.access(IGroupsReader.class, IUserMembershipReader.class, IGitReader.class, new IFunction3<IGroupsReader, IUserMembershipReader, IGitReader, Void>() {
			@Override
			public Void apply(IGroupsReader groupsReader, IUserMembershipReader userMembershipReader, IGitReader gitReader) throws Exception {
				gitReaderFn.apply(gitReader);
				return Functions.call(userMembershipReaderFn, groupsReader, userMembershipReader);
			}
		});
	}

	public void testGetSameReadWriters() {
		ICrowdSourcedApi api = getApi();
		final IContainer readWriter = api.makeContainer();

		final EnsureSameParameter<IUser> usersCallback = ICallback.Utils.ensureSameParameter();
		readWriter.modifyUser(usersCallback);
		readWriter.modify(IUser.class, usersCallback);
		readWriter.modifyUser(usersCallback);
		assertEquals(3, usersCallback.count.get());

		final EnsureSameParameter<IGroups> groupsCallback = ICallback.Utils.ensureSameParameter();
		readWriter.modifyGroups(groupsCallback);
		readWriter.modify(IGroups.class, groupsCallback);
		readWriter.modifyGroups(groupsCallback);
		assertEquals(3, groupsCallback.count.get());

		final ICallback2<IGroups, IUserMembership> userMembershipCallback = ICallback2.Utils.ensureSameParameters();
		readWriter.modifyUserMembership(userMembershipCallback);
		readWriter.modifyUserMembership(userMembershipCallback);
		readWriter.modify(IUserMembership.class, new ICallback<IUserMembership>() {
			@Override
			public void process(final IUserMembership userMembership) throws Exception {
				readWriter.modify(IGroups.class, new ICallback<IGroups>() {
					@Override
					public void process(IGroups groups) throws Exception {
						userMembershipCallback.process(groups, userMembership);
					}
				});
			}
		});

		readWriter.modify(IGroups.class, IUser.class, new ICallback2<IGroups, IUser>() {
			@Override
			public void process(IGroups groups, IUser user) throws Exception {
				groupsCallback.process(groups);
				usersCallback.process(user);
			}
		});

		readWriter.modify(IUserMembership.class, IUser.class, IGroups.class, new ICallback3<IUserMembership, IUser, IGroups>() {
			@Override
			public void process(IUserMembership userMembership, IUser user, IGroups groups) throws Exception {
				userMembershipCallback.process(groups, userMembership);
				usersCallback.process(user);
				groupsCallback.process(groups);
			}
		});

		EnsureSameParameter<IComments> commentsCallback = ICallback.Utils.ensureSameParameter();
		readWriter.modifyComments(commentsCallback);
		readWriter.modify(IComments.class, commentsCallback);
		readWriter.modifyComments(commentsCallback);
		assertEquals(3, commentsCallback.count.get());
	}

	public void testExceptionWhenNotRegistered() {
		ICrowdSourcedApi api = getApi();
		final ICrowdSourcedReaderApi reader = api.makeContainer();
		checkExceptionWhenAccessing(reader, Object.class, "Cannot access without registered reader for class class java.lang.Object. Legal readers are");
		checkExceptionWhenAccessing(reader, IUser.class, "Cannot access without registered reader for class interface org.softwareFm.crowdsource.api.user.IUser. Legal readers are ");

		final IContainer readWriter = api.makeContainer();
		checkExceptionWhenModifying(readWriter, Object.class, "Cannot modify without registered readWriter for class class java.lang.Object. Legal readWriters are ");
		checkExceptionWhenModifying(readWriter, IUserReader.class, "Cannot modify without registered readWriter for class interface org.softwareFm.crowdsource.api.user.IUserReader. Legal readWriters are ");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void checkExceptionWhenModifying(final IContainer readWriter, final Class<?> class1, String expectedMessage) {
		final ICallback callback = ICallback.Utils.exception("should not be called");
		NullPointerException e = Tests.assertThrows(NullPointerException.class, new Runnable() {
			@Override
			public void run() {
				readWriter.modify(class1, callback);
			}
		});
		assertTrue(e.getMessage(), e.getMessage().startsWith(expectedMessage));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void checkExceptionWhenAccessing(final ICrowdSourcedReaderApi reader, final Class<?> class1, String expectedMessage) {
		final IFunction1 expectionIfCalled = Functions.expectionIfCalled();
		NullPointerException e = Tests.assertThrows(NullPointerException.class, new Runnable() {
			@Override
			public void run() {
				reader.access(class1, expectionIfCalled);
			}
		});
		assertTrue(e.getMessage(), e.getMessage().startsWith(expectedMessage));
	}

}
