package org.softwareFm.server.processors;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.common.IUser;
import org.softwareFm.common.callbacks.ICallback;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.user.IProject;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.server.ICrowdSourcedServer;
import org.softwareFm.server.processors.internal.MailerMock;
import org.softwareFm.softwareFmServer.ProjectForServer;
import org.softwareFm.softwareFmServer.UsageProcessor;
import org.softwareFm.swt.ICollectionConfigurationFactory;
import org.softwareFm.swt.constants.CardConstants;
import org.springframework.jdbc.core.JdbcTemplate;

abstract public class AbstractProcessorDatabaseIntegrationTests extends AbstractProcessorIntegrationTests {
	private ICrowdSourcedServer server;
	protected JdbcTemplate template;
	private MailerMock mailerMock;
	protected String userKey;
	protected IFunction1<Map<String, Object>, String> cryptoFn;
	private Callable<String> cryptoGenerator;
	protected int thisDay;
	protected String projectCryptoKey;
	private IUser user;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		mailerMock = new MailerMock();
		userKey = Crypto.makeKey();
		cryptoFn = Functions.constant(userKey);
		cryptoGenerator = Callables.value(userKey);
		Callable<String> softwareFmIdGenerator = Callables.patternWithCount("someNewSoftwareFmId{0}");
		user = ICrowdSourcedServer.Utils.makeUserForServer(remoteOperations, Strings.firstNSegments(3));
		
		projectCryptoKey = Crypto.makeKey();

		IProcessCall processCalls = IProcessCall.Utils.softwareFmProcessCall(dataSource, remoteOperations, cryptoFn, cryptoGenerator, remoteRoot, mailerMock, softwareFmIdGenerator, getExtraProcessCalls());
		template = new JdbcTemplate(dataSource);
		server = ICrowdSourcedServer.Utils.testServerPort(processCalls, ICallback.Utils.rethrow());
		template.update("truncate users");
	}

	protected IProcessCall[] getExtraProcessCalls() {
		IUrlGenerator userUrlGenerator = ICollectionConfigurationFactory.Utils.makeSoftwareFmUrlGeneratorMap("softwareFm", "data").get(CardConstants.userUrlKey);
		IProject project = new ProjectForServer(remoteOperations, cryptoFn, user, userUrlGenerator, cryptoGenerator);
		IProjectTimeGetter projectTimeGetter= new IProjectTimeGetter() {
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
		return new IProcessCall[]{new UsageProcessor(remoteOperations, project, projectTimeGetter)};
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
