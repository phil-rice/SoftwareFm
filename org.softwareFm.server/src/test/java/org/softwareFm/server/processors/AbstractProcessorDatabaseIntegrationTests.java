package org.softwareFm.server.processors;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.server.IGitServer;
import org.softwareFm.server.ISoftwareFmServer;
import org.softwareFm.server.processors.internal.MailerMock;
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
	protected IGitServer gitServer;
	protected String key;
	protected IFunction1<Map<String, Object>, String> cryptoFn;
	protected Callable<String> monthGetter;
	protected Callable<Integer> dayGetter;
	private Callable<String> cryptoGenerator;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		mailerMock = new MailerMock();
		gitServer = IGitServer.Utils.gitServer(remoteRoot, "not used");
		key = Crypto.makeKey();
		cryptoFn = Functions.constant(key);
		cryptoGenerator = Callables.value(key);
		monthGetter = Callables.value("someMonth");
		dayGetter = Callables.value(3);
		Callable<String> softwareFmIdGenerator = Callables.patternWithCount("someNewSoftwareFmId{0}");
		IProcessCall result = IProcessCall.Utils.softwareFmProcessCall(dataSource, gitServer, cryptoFn, cryptoGenerator, remoteRoot, mailerMock, monthGetter, dayGetter, softwareFmIdGenerator, "g", "a");
		template = new JdbcTemplate(dataSource);
		server = ISoftwareFmServer.Utils.testServerPort(result, ICallback.Utils.rethrow());
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
