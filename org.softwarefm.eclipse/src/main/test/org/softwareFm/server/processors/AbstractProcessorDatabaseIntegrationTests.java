package org.softwareFm.server.processors;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.common.IUser;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.eclipse.project.UsageProcessor;
import org.softwareFm.eclipse.user.IProject;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.processors.internal.MailerMock;
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
		IUser user = IUser.Utils.makeUserForServer(remoteOperations, LoginConstants.userGenerator(), Strings.firstNSegments(3));
		projectCryptoKey = Crypto.makeKey();
		IProject project = IProject.Utils.makeProjectForServer(remoteOperations, cryptoFn, user, LoginConstants.userGenerator(), Callables.value(projectCryptoKey));
		UsageProcessor usageProcessor = new UsageProcessor(remoteOperations, project, new IProjectTimeGetter() {
			@Override
			public String thisMonth() {
				return "someMonth";
			}

			@Override
			public int day() {
				return thisDay;
			}

			@Override
			public Iterable<String> lastNMonths(int n) {
				throw new UnsupportedOperationException();
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
