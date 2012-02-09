package org.softwareFm.server.processors;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.common.IGroups;
import org.softwareFm.common.IUser;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.user.IProject;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.server.ICrowdSourcedServer;
import org.softwareFm.server.processors.internal.MailerMock;
import org.softwareFm.softwareFmServer.GroupForServer;
import org.softwareFm.softwareFmServer.ITakeOnProcessor;
import org.softwareFm.softwareFmServer.ProjectForServer;
import org.softwareFm.softwareFmServer.TakeOnGroupProcessor;
import org.softwareFm.softwareFmServer.TakeOnProcessor;
import org.softwareFm.softwareFmServer.UsageProcessor;
import org.springframework.jdbc.core.JdbcTemplate;

abstract public class AbstractProcessorDatabaseIntegrationTests extends AbstractProcessorIntegrationTests {
	private ICrowdSourcedServer server;
	protected JdbcTemplate template;
	protected MailerMock mailerMock;
	protected String userKey;
	protected IFunction1<Map<String, Object>, String> userCryptoFn;
	private Callable<String> userCryptoGenerator;
	protected int thisDay;
	protected String projectCryptoKey1;
	protected IUser user;
	protected IGroups groups;
	protected String groupId;
	private IUrlGenerator groupsGenerator;
	private IFunction1<String, String> repoDefnFn;
	protected String groupCryptoKey;
	private String userKey2;
	private String userKey3;
	protected String projectCryptoKey2;
	protected String projectCryptoKey3;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		mailerMock = new MailerMock();
		userKey = Crypto.makeKey();
		userKey2 = Crypto.makeKey();
		userKey3 = Crypto.makeKey();
		userCryptoFn = new IFunction1<Map<String,Object>, String>() {
			private final Map<String,Object>map = Maps.makeMap("someNewSoftwareFmId0", userKey,"someNewSoftwareFmId1", userKey2, "someNewSoftwareFmId2", userKey3);
			@Override
			public String apply(Map<String, Object> from) throws Exception {
				String softwareFmId = (String) from.get(LoginConstants.softwareFmIdKey);
				if (softwareFmId == null)
					throw new NullPointerException(from.toString());
				return (String) map.get(softwareFmId);
			}
		};
		userCryptoGenerator = Callables.valueFromList(userKey, userKey2, userKey3);
		Callable<String> softwareFmIdGenerator = Callables.patternWithCount("someNewSoftwareFmId{0}");
		user = ICrowdSourcedServer.Utils.makeUserForServer(remoteOperations, Strings.firstNSegments(3));
		projectCryptoKey1 = Crypto.makeKey();
		projectCryptoKey2 = Crypto.makeKey();
		projectCryptoKey3 = Crypto.makeKey();
		repoDefnFn = Strings.firstNSegments(3);
		groupsGenerator = GroupConstants.groupsGenerator();
		groupCryptoKey = Crypto.makeKey();
		groupId = "someGroupId";
		groups = new GroupForServer(groupsGenerator, remoteOperations, repoDefnFn);

		ProcessCallParameters processCallParameters = new ProcessCallParameters(dataSource, remoteOperations, userCryptoGenerator, softwareFmIdGenerator, mailerMock);
		IProcessCall processCalls = IProcessCall.Utils.softwareFmProcessCall(processCallParameters, getExtraProcessCalls());
		template = new JdbcTemplate(dataSource);
		server = ICrowdSourcedServer.Utils.testServerPort(processCalls, ICallback.Utils.rethrow());
		template.update("truncate users");
	}

	protected IFunction1<ProcessCallParameters, IProcessCall[]> getExtraProcessCalls() {
		return new IFunction1<ProcessCallParameters, IProcessCall[]>() {

			@Override
			public IProcessCall[] apply(ProcessCallParameters from) throws Exception {
				IUrlGenerator userUrlGenerator = LoginConstants.userGenerator();
				IProject project = new ProjectForServer(remoteOperations, userCryptoFn, user, userUrlGenerator, userCryptoGenerator);
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

				ITakeOnProcessor takeOnProcessor = new TakeOnProcessor(from.gitOperations, user, groups, userCryptoFn, emailToSfmId, Callables.valueFromList(projectCryptoKey1, projectCryptoKey2, projectCryptoKey3), groupsGenerator, groupIdGenerator, repoDefnFn);
				Callable<String> groupCryptoGenerator= Callables.value(groupCryptoKey);
				return new IProcessCall[] { //
				new UsageProcessor(remoteOperations, project, projectTimeGetter), //
						new TakeOnGroupProcessor(takeOnProcessor, from.signUpChecker, groupCryptoGenerator, emailToSfmId, from.saltGenerator, from.softwareFmIdGenerator, mailerMock) };
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
