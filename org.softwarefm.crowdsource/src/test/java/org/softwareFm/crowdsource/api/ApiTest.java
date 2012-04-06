package org.softwareFm.crowdsource.api;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.crowdsource.api.internal.CrowdSourcedLocalApi;
import org.softwareFm.crowdsource.api.internal.CrowdSourcedServerApi;
import org.softwareFm.crowdsource.api.server.GitWithHttpClientTest;
import org.softwareFm.crowdsource.api.server.IServerDoers;
import org.softwareFm.crowdsource.api.server.IUsage;
import org.softwareFm.crowdsource.api.server.SignUpResult;
import org.softwareFm.crowdsource.api.user.IUser;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.processors.AbstractLoginDataAccessor;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManager;
import org.softwareFm.crowdsource.utilities.transaction.ITransactionManagerBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

abstract public class ApiTest extends GitWithHttpClientTest {
	// these are returned by the cryptoGenerators
	protected final static String userKey0 = Crypto.makeKey();
	protected final static String userKey1 = Crypto.makeKey();
	protected final static String userKey2 = Crypto.makeKey();
	protected final static String userKey3 = Crypto.makeKey();

	// these are returned by the cryptoGenerators
	protected final static String groupCryptoKey0 = Crypto.makeKey();
	protected final static String groupCryptoKey1 = Crypto.makeKey();
	protected final static String groupCryptoKey2 = Crypto.makeKey();

	private final static String softwareFmIdPrefix = "someNewSoftwareFmId";

	// these are the results of the idAndSaltGenerator
	protected final static String softwareFmId0 = softwareFmIdPrefix + "0";
	protected final static String softwareFmId1 = softwareFmIdPrefix + "1";
	protected final static String softwareFmId2 = softwareFmIdPrefix + "2";

	// these are the emails created by createUser
	protected final static String email0 = softwareFmId0 + "@someEmail.com";
	protected final static String email1 = softwareFmId1 + "@someEmail.com";
	protected final static String email2 = softwareFmId2 + "@someEmail.com";

	// these are the results of the idAndSaltGenerator
	protected final static String groupId0 = "groupId0";
	protected final static String groupId1 = "groupId1";
	protected final static String groupId2 = "groupId2";

	// these are suggested group names
	protected final static String groupName0 = "groupId0Name";
	protected final static String groupName1 = "groupId1Name";
	protected final static String groupName2 = "groupId2Name";

	protected final static String someMoniker = "someMoniker";

	protected BasicDataSource dataSource;

	private ICryptoGenerators cryptoGenerators;
	private IUserCryptoAccess userCryptoAccess;
	private IIdAndSaltGenerator idAndSaltGenerator;
	private ServerConfig serverConfig;
	private LocalConfig localConfig;
	private CrowdSourcedServerApi serverApi;
	private CrowdSourcedLocalApi localApi;
	private JdbcTemplate template;
	private ITransactionManagerBuilder localTransactionManager;
	private ITransactionManagerBuilder serverTransactionManager;

	protected JdbcTemplate getTemplate() {
		return template == null ? new JdbcTemplate(dataSource) : template;
	}

	protected IServerDoers getServerDoers() {
		return ((CrowdSourcedServerApi) getServerApi()).getServerDoers();
	}

	protected ICrowdSourcedApi getServerApi() {
		return serverApi == null ? serverApi = (CrowdSourcedServerApi) ICrowdSourcedApi.Utils.forServer(getServerConfig(), getServerTransactionManager()) : serverApi;
	}

	protected ITransactionManager getServerTransactionManager() {
		return serverTransactionManager == null ? serverTransactionManager = ITransactionManager.Utils.standard(CommonConstants.serverThreadPoolSizeForTests) : serverTransactionManager;
	}

	protected ITransactionManager getLocalTransactionManager() {
		return localTransactionManager == null ? localTransactionManager = ITransactionManager.Utils.standard(CommonConstants.localThreadPoolSizeForTests) : localTransactionManager;
	}

	protected ICrowdSourcedApi getLocalApi() {
		return localApi == null ? localApi = (CrowdSourcedLocalApi) ICrowdSourcedApi.Utils.forClient(getLocalConfig(), getLocalTransactionManager()) : localApi;
	}

	protected ServerConfig getServerConfig() {
		return serverConfig == null ? serverConfig = new ServerConfig(CommonConstants.testPort, 10, CommonConstants.testTimeOutMs, remoteRoot, dataSource, //
				getTakeOnEnrichment(), getExtraProcessCalls(), getUsage(), getIdAndSaltGenerator(), getCryptoGenerators(), //
				getUserCryptoAccess(), getUrlPrefix(), getDefaultUserValues(), getDefaultGroupValues(), getErrorHandler(), getMailer(), getTimeGetter(), getServerExtraReaderWriterConfigurator()) : serverConfig;
	}

	protected LocalConfig getLocalConfig() {
		return localConfig == null ? localConfig = new LocalConfig(CommonConstants.testPort, 20, CommonConstants.testTimeOutMs, "localhost", localRoot, getUrlPrefix(), remoteAsUri, CommonConstants.staleCachePeriodForTest,  getErrorHandler(), getLocalExtraReaderWriterConfigurator()) : localConfig;
	} 

	protected IExtraReaderWriterConfigurator<ServerConfig> getServerExtraReaderWriterConfigurator() {
		return IExtraReaderWriterConfigurator.Utils.noExtras();
	}

	protected IExtraReaderWriterConfigurator<LocalConfig> getLocalExtraReaderWriterConfigurator() {
		return IExtraReaderWriterConfigurator.Utils.noExtras();
	}

	protected IUserCryptoAccess getUserCryptoAccess() {
		return userCryptoAccess == null ? userCryptoAccess = IUserCryptoAccess.Utils.database(dataSource, idAndSaltGenerator) : userCryptoAccess;
	}

	protected ICryptoGenerators getCryptoGenerators() {
		return cryptoGenerators == null ? cryptoGenerators = ICryptoGenerators.Utils.mock(//
				new String[] { userKey0, userKey1, userKey2, userKey3 }, //
				new String[] { groupCryptoKey0, groupCryptoKey1, groupCryptoKey2 }) : cryptoGenerators;
	}

	protected IIdAndSaltGenerator getIdAndSaltGenerator() {
		return idAndSaltGenerator == null ? idAndSaltGenerator = IIdAndSaltGenerator.Utils.mockGenerators(softwareFmIdPrefix + "{0}", "groupId{0}", "salt{0}", "magicString{0}") : idAndSaltGenerator;
	}

	protected Callable<Long> getTimeGetter() {
		return Callables.valueFromList(1000l, 2000l, 3000l);
	}

	protected IUsage getUsage() {
		return IUsage.Utils.noUsage();
	}

	protected ICallback<Throwable> getErrorHandler() {
		return ICallback.Utils.rethrow();
	}

	protected Map<String, Callable<Object>> getDefaultUserValues() {
		return Maps.makeMap(GroupConstants.membershipCryptoKey, Callables.makeCryptoKey(), CommentConstants.commentCryptoKey, Callables.makeCryptoKey());
	}

	protected Map<String, Callable<Object>> getDefaultGroupValues() {
		return Maps.makeMap(CommentConstants.commentCryptoKey, Callables.makeCryptoKey());
	}

	protected IExtraCallProcessorFactory getExtraProcessCalls() {
		return IExtraCallProcessorFactory.Utils.noExtraCalls();
	}

	private int takeOnCount;
	protected MailerMock mailer;

	protected ITakeOnEnrichmentProvider getTakeOnEnrichment() {
		return new ITakeOnEnrichmentProvider() {
			@Override
			public Map<String, Object> takeOn(Map<String, Object> initial, String userCrypto, IUserAndGroupsContainer container) {
				return Maps.with(initial, "with", "enrich_" + takeOnCount++);
			}
		};
	}

	protected String getUrlPrefix() {
		return "prefix";
	}

	protected MailerMock getMailer() {
		return mailer;
	}

	@Override
	protected void setUp() throws Exception {
		mailer = new MailerMock();
		dataSource = AbstractLoginDataAccessor.defaultDataSource();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		if (serverApi != null)
			serverApi.shutdown();
		if (localApi != null)
			localApi.shutdown();
		idAndSaltGenerator = null;
		userCryptoAccess = null;
		serverApi = null;
		cryptoGenerators = null;
		serverConfig = null;
		localApi = null;
	}

	protected void truncateUsersTable() {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		template.update("delete from users");
	}

	protected String createUser() {
		final String userId = getIdAndSaltGenerator().makeNewUserId();
		final String email = userId + "@someEmail.com";
		createUser(userId, email);
		return userId;
	}

	protected void createUser(final String userId, final String email) {
		getServerApi().makeUserAndGroupsContainer().accessUser(new ICallback<IUser>() {
			@Override
			public void process(IUser user) throws Exception {
				SignUpResult signUp = serverApi.getServerDoers().getSignUpChecker().signUp(email, someMoniker, "someSalt", "passHash", userId);
				String crypto = signUp.crypto;
				if (crypto == null)
					fail(signUp.errorMessage);
				user.setUserProperty(userId, crypto, LoginConstants.emailKey, email);
			}
		}).get();
	}

	protected String createGroup(String groupName, String groupCrypto) {
		return getServerDoers().getTakeOnProcessor().createGroup(groupName, groupCrypto);
	}

	protected String getUserProperty(final String softwareFmId, final String userCrypto, final String property) {
		return getServerUserAndGroupsContainer().accessUserReader(new IFunction1<IUserReader, String>() {
			@Override
			public String apply(IUserReader from) throws Exception {
				return from.getUserProperty(softwareFmId, userCrypto, property);
			}
		}, ICallback.Utils.<String> noCallback()).get();
	}

	protected IContainer getServerContainer() {
		return getServerApi().makeContainer();
	}

	protected IUserAndGroupsContainer getServerUserAndGroupsContainer() {
		return getServerApi().makeUserAndGroupsContainer();
	}

	protected IUserAndGroupsContainer getLocalUserAndGroupsContainer() {
		return getLocalApi().makeUserAndGroupsContainer();
	}

	protected IContainer getLocalContainer() {
		return getLocalApi().makeContainer();
	}

}
