package org.softwareFm.crowdsource.api.internal;

import org.softwareFm.crowdsource.api.ApiTest;
import org.softwareFm.crowdsource.api.ICommentsReader;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;
import org.softwareFm.crowdsource.utilities.functions.IFunction3;
import org.softwareFm.crowdsource.utilities.tests.Tests;

abstract public class AbstractCrowdReadWriterApiTest extends ApiTest {
	abstract protected ICrowdSourcedApi getApi();

	public void testGetSameReads() {
		final IUserAndGroupsContainer container = getApi().makeUserAndGroupsContainer();

		IFunction1<ICommentsReader, Integer> commentsFn = Functions.ensureSameParameters();
		container.accessCommentsReader(commentsFn, ICallback.Utils.<Integer> noCallback()).get();
		container.access(ICommentsReader.class, commentsFn, ICallback.Utils.<Integer> noCallback()).get();
		assertEquals(3, container.accessCommentsReader(commentsFn, ICallback.Utils.<Integer> noCallback()).get(CommonConstants.testTimeOutMs).intValue());

		IFunction1<IUserReader, Integer> userReaderFn = Functions.ensureSameParameters();
		container.accessUserReader(userReaderFn, ICallback.Utils.<Integer> noCallback()).get();
		container.access(IUserReader.class, userReaderFn, ICallback.Utils.<Integer> noCallback()).get();
		assertEquals(3, container.accessUserReader(userReaderFn, ICallback.Utils.<Integer> noCallback()).get().intValue());

		IFunction1<IGroupsReader, Integer> groupsReaderFn = Functions.ensureSameParameters();
		container.accessGroupReader(groupsReaderFn, ICallback.Utils.<Integer> noCallback()).get();
		container.access(IGroupsReader.class, groupsReaderFn, ICallback.Utils.<Integer> noCallback()).get();
		assertEquals(3, container.accessGroupReader(groupsReaderFn, ICallback.Utils.<Integer> noCallback()).get().intValue());

		final IFunction2<IGroupsReader, IUserMembershipReader, Void> userMembershipReaderFn = Functions.ensureSameParameters2();
		container.accessUserMembershipReader(userMembershipReaderFn, ICallback.Utils.<Void> noCallback()).get();
		container.access(IUserMembershipReader.class, new IFunction1<IUserMembershipReader, Void>() {
			@Override
			public Void apply(final IUserMembershipReader userMembershipReader) throws Exception {
				container.access(IGroupsReader.class, new IFunction1<IGroupsReader, Void>() {
					@Override
					public Void apply(IGroupsReader groupsReader) throws Exception {
						return userMembershipReaderFn.apply(groupsReader, userMembershipReader);
					}
				}, ICallback.Utils.<Void> noCallback());
				return null;
			}
		}, ICallback.Utils.<Void> noCallback()).get();
		container.accessUserMembershipReader(userMembershipReaderFn, ICallback.Utils.<Void> noCallback());

		final IFunction1<IGitReader, Integer> gitReaderFn = Functions.ensureSameParameters();
		container.accessGitReader(gitReaderFn, ICallback.Utils.<Integer> noCallback()).get();
		container.access(IGitReader.class, gitReaderFn, ICallback.Utils.<Integer> noCallback()).get();
		assertEquals(3, container.accessGitReader(gitReaderFn, ICallback.Utils.<Integer> noCallback()).get(CommonConstants.testTimeOutMs).intValue());

		container.access(IGroupsReader.class, IUserMembershipReader.class, new IFunction2<IGroupsReader, IUserMembershipReader, Void>() {
			@Override
			public Void apply(IGroupsReader groupsReader, IUserMembershipReader userMembershipReader) throws Exception {
				return userMembershipReaderFn.apply(groupsReader, userMembershipReader);
			}
		}, ICallback.Utils.<Void> noCallback()).get();

		container.access(IGroupsReader.class, IUserMembershipReader.class, IGitReader.class, new IFunction3<IGroupsReader, IUserMembershipReader, IGitReader, Void>() {
			@Override
			public Void apply(IGroupsReader groupsReader, IUserMembershipReader userMembershipReader, IGitReader gitReader) throws Exception {
				gitReaderFn.apply(gitReader);
				return Functions.call(userMembershipReaderFn, groupsReader, userMembershipReader);
			}
		}, ICallback.Utils.<Void> noCallback()).get();
	}

	public void testExceptionWhenNotRegistered() {
		ICrowdSourcedApi api = getApi();
		final IContainer container = api.makeContainer();

		checkExceptionWhenAccessingFunction(container, Object.class, "Cannot modify without registered readWriter for class class java.lang.Object. Legal readWriters are");
		checkExceptionWhenModifyingCallback(container, Object.class, "Cannot modify without registered readWriter for class class java.lang.Object. Legal readWriters are ");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void checkExceptionWhenModifyingCallback(final IContainer container, final Class<?> class1, String expectedMessage) {
		final ICallback callback = ICallback.Utils.exception("should not be called");
		NullPointerException e = Tests.assertThrows(NullPointerException.class, new Runnable() {
			@Override
			public void run() {
				container.access(class1, callback).get();
			}
		});
		assertTrue(e.getMessage(), e.getMessage().startsWith(expectedMessage));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void checkExceptionWhenAccessingFunction(final IContainer container, final Class<?> class1, String expectedMessage) {
		final IFunction1 expectionIfCalled = Functions.expectionIfCalled();
		NullPointerException e = Tests.assertThrows(NullPointerException.class, new Runnable() {
			@Override
			public void run() {
				container.access(class1, expectionIfCalled, ICallback.Utils.<Void> exception("dont call")).get();
			}
		});
		assertTrue(e.getMessage(), e.getMessage().startsWith(expectedMessage));
	}

}
