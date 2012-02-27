/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGroups;
import org.softwareFm.common.IUser;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.future.Futures;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.AbstractJarToGroupArtifactVersion;
import org.softwareFm.eclipse.IGroupArtifactVersionCallback;
import org.softwareFm.eclipse.IJarToGroupArtifactAndVersion;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IProject;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.server.ICrowdSourcedServer;
import org.softwareFm.server.comments.CommentsForServer;
import org.softwareFm.server.comments.IComments;
import org.softwareFm.server.processors.internal.MailerMock;
import org.softwareFm.softwareFmServer.GroupsForServer;
import org.softwareFm.softwareFmServer.ITakeOnProcessor;
import org.softwareFm.softwareFmServer.ProjectForServer;
import org.softwareFm.softwareFmServer.TakeOnGroupProcessor;
import org.softwareFm.softwareFmServer.TakeOnProcessor;
import org.softwareFm.softwareFmServer.UsageProcessor;
import org.softwareFm.softwareFmServer.UserMembershipForServer;
import org.springframework.jdbc.core.JdbcTemplate;

abstract public class AbstractProcessorDatabaseIntegrationTests extends AbstractProcessorIntegrationTests {
	private ICrowdSourcedServer server;
	protected JdbcTemplate template;
	protected MailerMock mailerMock;
	protected String userKey;
	protected IFunction1<Map<String, Object>, String> userCryptoFn;
	protected Callable<String> userCryptoGenerator;
	protected int thisDay;
	protected String projectCryptoKey1;
	protected IGroups groups;
	protected String groupId;
	private IUrlGenerator groupsGenerator;
	private IFunction1<String, String> repoDefnFn;
	protected String groupCryptoKey;
	private String userKey2;
	private String userKey3;
	protected String projectCryptoKey2;
	protected String projectCryptoKey3;

	protected UserMembershipForServer membershipForServer;
	protected ITakeOnProcessor takeOnProcessor;
	protected IComments comments;
	protected IUser user;
	protected Callable<String> softwareFmIdGenerator;
	protected ProcessCallParameters processCallParameters;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		mailerMock = new MailerMock();
		userKey = Crypto.makeKey();
		userKey2 = Crypto.makeKey();
		userKey3 = Crypto.makeKey();
		userCryptoFn = new IFunction1<Map<String, Object>, String>() {
			private final Map<String, Object> map = Maps.makeMap("someNewSoftwareFmId0", userKey, "someNewSoftwareFmId1", userKey2, "someNewSoftwareFmId2", userKey3);
			@Override
			public String apply(Map<String, Object> from) throws Exception {
				String softwareFmId = (String) from.get(LoginConstants.softwareFmIdKey);
				if (softwareFmId == null)
					throw new NullPointerException(from.toString());
				return (String) map.get(softwareFmId);
			}
		};
		userCryptoGenerator = Callables.valueFromList(userKey, userKey2, userKey3);
		softwareFmIdGenerator = Callables.patternWithCount("someNewSoftwareFmId{0}");
		projectCryptoKey1 = Crypto.makeKey();
		projectCryptoKey2 = Crypto.makeKey();
		projectCryptoKey3 = Crypto.makeKey();
		repoDefnFn = Strings.firstNSegments(3);
		groupsGenerator = GroupConstants.groupsGenerator(SoftwareFmConstants.urlPrefix);
		groupCryptoKey = Crypto.makeKey();
		groupId = "someGroupId";
		groups = new GroupsForServer(groupsGenerator, remoteOperations, repoDefnFn);

		Map<String, Callable<Object>> defaultValues = Maps.makeMap(//
				GroupConstants.membershipCryptoKey, Callables.makeCryptoKey(), //
				GroupConstants.groupCryptoKey, Callables.valueFromList(groupCryptoKey),//
				SoftwareFmConstants.projectCryptoKey, Callables.valueFromList(projectCryptoKey1, projectCryptoKey2, projectCryptoKey3));
		processCallParameters = new ProcessCallParameters(dataSource, remoteOperations, userCryptoGenerator, softwareFmIdGenerator, userCryptoFn, mailerMock, defaultValues, SoftwareFmConstants.urlPrefix);
		IProcessCall processCalls = IProcessCall.Utils.softwareFmProcessCall(processCallParameters, getExtraProcessCalls());
		template = new JdbcTemplate(dataSource);
		server = ICrowdSourcedServer.Utils.testServerPort(processCalls, ICallback.Utils.rethrow());
		template.update("truncate users");
	}

	protected IFunction1<ProcessCallParameters, IProcessCall[]> getExtraProcessCalls() {
		return new IFunction1<ProcessCallParameters, IProcessCall[]>() {
			@Override
			public IProcessCall[] apply(ProcessCallParameters from) throws Exception {
				IUrlGenerator userUrlGenerator = LoginConstants.userGenerator(SoftwareFmConstants.urlPrefix);
				IUrlGenerator jarUrlGenerator = SoftwareFmConstants.jarUrlGenerator(SoftwareFmConstants.urlPrefix);
				IProject project = new ProjectForServer(remoteOperations, userCryptoFn, from.user, userUrlGenerator);
				IProjectTimeGetter projectTimeGetter = new IProjectTimeGetter() {
					@Override
					public String thisMonth() {
						return "someMonth";
					}

					@Override
					public Iterable<String> lastNMonths(int n) {
						throw new UnsupportedOperationException();
					}

					@Override
					public int day() {
						return thisDay;
					}
				};

				IFunction1<String, String> emailToSfmId = ICrowdSourcedServer.Utils.emailToSoftwareFmId(from.dataSource);
				Callable<String> groupIdGenerator = Callables.value(groupId);

				membershipForServer = new UserMembershipForServer(userUrlGenerator, from.user, from.gitOperations, emailToSfmId);
				takeOnProcessor = new TakeOnProcessor(from.gitOperations, from.user, membershipForServer, groups, userCryptoFn, groupsGenerator, groupIdGenerator, repoDefnFn);
				Callable<String> groupCryptoGenerator = Callables.value(groupCryptoKey);
				comments = new CommentsForServer(from.gitOperations, from.user, membershipForServer, groups, Callables.value(1000l));
				user = from.user;
				IProcessCall[] result = new IProcessCall[] { //
				new CommentProcessor(from.user, membershipForServer, groups, comments, from.userCryptoFn),//
						new UsageProcessor(remoteOperations, getJarToGroupArtifactAndVersionProcessor(jarUrlGenerator), project, projectTimeGetter), //
						new TakeOnGroupProcessor(takeOnProcessor, from.signUpChecker, groupCryptoGenerator, emailToSfmId, from.saltGenerator, from.softwareFmIdGenerator, mailerMock) };
				return result;
			}

		};
	}

	protected IJarToGroupArtifactAndVersion getJarToGroupArtifactAndVersionProcessor(IUrlGenerator jarUrlGenerator) {
		return new AbstractJarToGroupArtifactVersion(jarUrlGenerator) {
			Map<String, Map<String, Object>> jarMap = Maps.makeMap(//
					"digest11", make("someGroupId1", "someArtifactId1", "someVersion1"),//
					"digest12", make("someGroupId1", "someArtifactId2", "someVersion1"),//
					"digest21", make("someGroupId2", "someArtifactId1", "someVersion1"),//
					"digest22", make("someGroupId2", "someArtifactId2", "someVersion1"),//
					"digest23", make("someGroupId2", "someArtifactId3", "someVersion1"));

			@Override
			protected Future<?> processMapFrom(IFileDescription fileDescription, IGroupArtifactVersionCallback callback) {
				String url = fileDescription.url();
				String digest = Strings.lastSegment(url, "/");
				replyTo(callback, jarMap.get(digest));
				return Futures.doneFuture(null);
			}

			private Map<String, Object> make(String groupId, String artifactId, String version) {
				return Maps.makeMap(SoftwareFmConstants.groupIdKey, groupId, SoftwareFmConstants.artifactIdKey, artifactId, SoftwareFmConstants.version, version);
			}
		};
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		server.shutdown();
	}

	protected String getPasswordResetKeyFor(String email) {
		return template.queryForObject("select passwordResetKey from users where email=?", String.class, email);
	}
}