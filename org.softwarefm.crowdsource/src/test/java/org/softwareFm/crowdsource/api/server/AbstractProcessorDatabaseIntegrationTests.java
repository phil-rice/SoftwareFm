/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.server;

import org.softwareFm.crowdsource.api.ICrowdSourceReadWriteApi;
import org.softwareFm.crowdsource.api.ICrowdSourcedServer;
import org.softwareFm.crowdsource.api.MailerMock;
import org.softwareFm.crowdsource.api.internal.CrowdSourcedServerApi;

abstract public class AbstractProcessorDatabaseIntegrationTests extends AbstractProcessorIntegrationTests {
	private ICrowdSourcedServer server;
	protected MailerMock mailerMock;
	protected int thisDay;

	public CrowdSourcedServerApi api;
	public ICrowdSourceReadWriteApi readWriter;

	@Override
	protected IMailer getMailer() {
		return mailerMock = new MailerMock();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		api = (CrowdSourcedServerApi) getServerApi();
		readWriter = api.makeReadWriter();
		server = api.getServer();
		truncateUsersTable();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		server.shutdown();
		dataSource.close();
	}

	protected String getPasswordResetKeyFor(String email) {
		return getTemplate().queryForObject("select passwordResetKey from users where email=?", String.class, email);
	}
}

// @Override
// public ICallProcessor[] apply(ProcessCallParameters from) throws Exception {
// IUrlGenerator jarUrlGenerator = SoftwareFmConstants.jarUrlGenerator(SoftwareFmConstants.urlPrefix);
// IProject project = new ProjectForServer(remoteOperations, userCryptoFn, from.user, userUrlGenerator);
// IProjectTimeGetter projectTimeGetter = new IProjectTimeGetter() {
// @Override
// public String thisMonth() {
// return "someMonth";
// }
//
// @Override
// public Iterable<String> lastNMonths(int n) {
// throw new UnsupportedOperationException();
// }
//
// @Override
// public int day() {
// return thisDay;
// }
// };
//
// IFunction1<String, String> emailToSfmId = ICrowdSourcedServer.Utils.emailToSoftwareFmId(from.dataSource);
// Callable<String> groupIdGenerator = Callables.value(groupId);
//
// membershipForServer = new UserMembershipForServer(userUrlGenerator, from.user, from.gitOperations, emailToSfmId);
// takeOnProcessor = new TakeOnProcessor(from.gitOperations, from.user, membershipForServer, groups, userCryptoFn, groupsGenerator, groupIdGenerator, repoDefnFn);
// Callable<String> groupCryptoGenerator = Callables.value(groupCryptoKey);
// comments = new CommentsForServer(from.gitOperations, from.user, membershipForServer, groups, Callables.value(1000l));
// user = from.user;
// ICallProcessor[] result = new ICallProcessor[] { //
// new CommentProcessor(from.user, membershipForServer, groups, comments, from.userCryptoFn),//
// new TakeOnGroupProcessor(takeOnProcessor, from.signUpChecker, groupCryptoGenerator, emailToSfmId, from.saltGenerator, from.softwareFmIdGenerator, mailerMock),//
// new InviteGroupProcessor(takeOnProcessor, from.signUpChecker, emailToSfmId, from.saltGenerator, softwareFmIdGenerator, mailerMock, userCryptoFn, membershipForServer, groups),//
// new AcceptInviteGroupProcessor(groups, membershipForServer, userCryptoFn) };
// return result;
// }
//
// };

//
// @Override
// protected Future<?> processMapFrom(IFileDescription fileDescription, IGroupArtifactVersionCallback callback) {
// String url = fileDescription.url();
// String digest = Strings.lastSegment(url, "/");
// replyTo(callback, jarMap.get(digest));
// return Futures.doneFuture(null);
// }
//
// private Map<String, Object> make(String groupId, String artifactId, String version) {
// return Maps.makeMap(SoftwareFmConstants.groupIdKey, groupId, SoftwareFmConstants.artifactIdKey, artifactId, SoftwareFmConstants.version, version);
// }
// };
// }