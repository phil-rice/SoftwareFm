/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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
		container.access(ICommentsReader.class, commentsFn).get();
		container.accessWithCallback(ICommentsReader.class, commentsFn, ICallback.Utils.<Integer> noCallback()).get();
		assertEquals(4, container.accessCommentsReader(commentsFn, ICallback.Utils.<Integer> noCallback()).get(CommonConstants.testTimeOutMs).intValue());

		IFunction1<IUserReader, Integer> userReaderFn = Functions.ensureSameParameters();
		container.accessUserReader(userReaderFn, ICallback.Utils.<Integer> noCallback()).get();
		container.access(IUserReader.class, userReaderFn).get();
		container.accessWithCallback(IUserReader.class, userReaderFn, ICallback.Utils.<Integer> noCallback()).get();
		assertEquals(4, container.accessUserReader(userReaderFn, ICallback.Utils.<Integer> noCallback()).get().intValue());

		IFunction1<IGroupsReader, Integer> groupsReaderFn = Functions.ensureSameParameters();
		container.accessGroupReader(groupsReaderFn, ICallback.Utils.<Integer> noCallback()).get();
		container.accessWithCallback(IGroupsReader.class, groupsReaderFn, ICallback.Utils.<Integer> noCallback()).get();
		container.access(IGroupsReader.class, groupsReaderFn).get();
		assertEquals(4, container.accessGroupReader(groupsReaderFn, ICallback.Utils.<Integer> noCallback()).get().intValue());

		final IFunction2<IGroupsReader, IUserMembershipReader, Void> userMembershipReaderFn = Functions.ensureSameParameters2();
		container.accessUserMembershipReader(userMembershipReaderFn, ICallback.Utils.<Void> noCallback()).get();
		container.accessWithCallback(IUserMembershipReader.class, new IFunction1<IUserMembershipReader, Void>() {
			@Override
			public Void apply(final IUserMembershipReader userMembershipReader) throws Exception {
				container.accessWithCallback(IGroupsReader.class, new IFunction1<IGroupsReader, Void>() {
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
		container.access(IGitReader.class, gitReaderFn).get();
		container.accessWithCallback(IGitReader.class, gitReaderFn, ICallback.Utils.<Integer> noCallback()).get();
		assertEquals(4, container.accessGitReader(gitReaderFn, ICallback.Utils.<Integer> noCallback()).get(CommonConstants.testTimeOutMs).intValue());

		container.accessWithCallback(IGroupsReader.class, IUserMembershipReader.class, new IFunction2<IGroupsReader, IUserMembershipReader, Void>() {
			@Override
			public Void apply(IGroupsReader groupsReader, IUserMembershipReader userMembershipReader) throws Exception {
				return userMembershipReaderFn.apply(groupsReader, userMembershipReader);
			}
		}, ICallback.Utils.<Void> noCallback()).get();

		container.accessWithCallback(IGroupsReader.class, IUserMembershipReader.class, IGitReader.class, new IFunction3<IGroupsReader, IUserMembershipReader, IGitReader, Void>() {
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
				container.access(class1, expectionIfCalled).get();
			}
		});
		assertTrue(e.getMessage(), e.getMessage().startsWith(expectedMessage));
	}

}