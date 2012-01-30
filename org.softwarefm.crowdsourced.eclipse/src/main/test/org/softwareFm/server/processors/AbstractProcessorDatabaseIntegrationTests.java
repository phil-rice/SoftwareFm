package org.softwareFm.server.processors;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.IUser;
import org.softwareFm.server.constants.LoginConstants;
import org.softwareFm.server.processors.internal.MailerMock;
import org.softwareFm.server.processors.internal.UsageProcessor;
import org.softwareFm.server.user.IProject;
import org.softwareFm.server.user.IProjectTimeGetter;
import org.softwareFm.utilities.callbacks.ICallback;
import org.softwareFm.utilities.crypto.Crypto;
import org.softwareFm.utilities.functions.Functions;
import org.softwareFm.utilities.functions.IFunction1;
import org.softwareFm.utilities.runnable.Callables;
import org.springframework.jdbc.core.JdbcTemplate;

abstract public class AbstractProcessorDatabaseIntegrationTests extends AbstractProcessorIntegrationTests {
	private ISoftwareFmServer server;
	protected JdbcTemplate template;
	private MailerMock mailerMock;
	protected String userKey;
	protected IFunction1<Map<String, Object>, String> cryptoFn;
	private Callable<String> cryptoGenerator;
	protected int thisDay;
	protected String projectCryptoKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		mailerMock = new MailerMock();
		userKey = Crypto.makeKey();
		cryptoFn = Functions.constant(userKey);
		cryptoGenerator = Callables.value(userKey);
		Callable<String> softwareFmIdGenerator = Callables.patternWithCount("someNewSoftwareFmId{0}");
		IUser user = IUser.Utils.makeUserForServer(remoteOperations, LoginConstants.userGenerator(), findRepositoryRoot);
		projectCryptoKey = Crypto.makeKey();
		IProject project = IProject.Utils.makeProjectForServer(remoteOperations, cryptoFn, user, LoginConstants.userGenerator(), Callables.value(projectCryptoKey));
		UsageProcessor usageProcessor = new UsageProcessor(remoteOperations, project, new IProjectTimeGetter() {
			@Override
			public String month() {
				return "someMonth";
			}

			@Override
			public int day() {
				return thisDay;
			}
		});
		IProcessCall processCalls = IProcessCall.Utils.softwareFmProcessCall(dataSource, remoteOperations, cryptoFn, cryptoGenerator, remoteRoot, mailerMock, softwareFmIdGenerator, usageProcessor);
		template = new JdbcTemplate(dataSource);
		server = ISoftwareFmServer.Utils.testServerPort(processCalls, ICallback.Utils.rethrow());
		template.update("truncate users");
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
