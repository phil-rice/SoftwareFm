package org.softwareFm.crowdsource.api;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.crowdsource.api.server.IMailer;
import org.softwareFm.crowdsource.api.server.IUsage;
import org.softwareFm.crowdsource.constants.CommentConstants;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.maps.UrlCache;
import org.softwareFm.crowdsource.utilities.processors.AbstractLoginDataAccessor;
import org.softwareFm.crowdsource.utilities.runnable.Callables;
import org.softwareFm.crowdsource.utilities.strings.Strings;

public class ServerConfig extends ApiConfig {

	@SuppressWarnings("rawtypes")
	public static ServerConfig serverConfigForTests(File root, IMailer mailer) {
		ITakeOnEnrichmentProvider takeOnEnrichment = ITakeOnEnrichmentProvider.Utils.noEnrichment();
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		Map<String, Callable<Object>> defaultUserValues = Maps.makeMap(CommentConstants.commentCryptoKey, Callables.makeCryptoKey());
		Map<String, Callable<Object>> defaultGroupValues = Maps.makeMap(CommentConstants.commentCryptoKey, Callables.makeCryptoKey());
		IIdAndSaltGenerator idGenerators = IIdAndSaltGenerator.Utils.uuidGenerators();
		ICryptoGenerators cryptoGenerators = ICryptoGenerators.Utils.cryptoGenerators();
		IUserCryptoAccess userCryptoAccess = IUserCryptoAccess.Utils.database(dataSource, idGenerators);

		return new ServerConfig(CommonConstants.testPort, 10, CommonConstants.testTimeOutMs, root, dataSource, takeOnEnrichment, IExtraCallProcessorFactory.Utils.noExtraCalls(), IUsage.Utils.noUsage(),//
				idGenerators, cryptoGenerators, userCryptoAccess, "testPrefix", defaultUserValues, defaultGroupValues, ICallback.Utils.rethrow(), mailer, Callables.valueFromList(1000l, 2000l, 3000l, 4000l), IExtraReaderWriterConfigurator.Utils.<ServerConfig> noExtras());
	}

	public final Map<String, Callable<Object>> defaultUserValues;
	public final Map<String, Callable<Object>> defaultGroupProperties;
	@SuppressWarnings("migrate away from this towarsd the usercryptoaccess")
	public final BasicDataSource dataSource;
	public final ITakeOnEnrichmentProvider takeOnEnrichment;
	public final IExtraCallProcessorFactory extraCallProcessors;
	public final IUsage usage;

	public final IIdAndSaltGenerator idAndSaltGenerator;
	public final ICryptoGenerators cryptoGenerators;
	public final IUserCryptoAccess userCryptoAccess;

	public final UrlCache<String> aboveRepostoryUrlCache = new UrlCache<String>();

	public final IFunction1<String, String> userRepoDefnFn = Strings.firstNSegments(3);
	public final IFunction1<String, String> groupRepoDefnFn = Strings.firstNSegments(3);
	public final Callable<Long> timeGetter;

	public final IMailer mailer;
	
	public ServerConfig(int port, int workerThreads,long timeOutMs,  File root, BasicDataSource dataSource, ITakeOnEnrichmentProvider takeOnEnrichment, IExtraCallProcessorFactory extraCallProcessors, IUsage usage, IIdAndSaltGenerator idAndSaltGenerator, ICryptoGenerators cryptoGenerators, IUserCryptoAccess userCryptoAccess, String prefix, Map<String, Callable<Object>> defaultUserValues, Map<String, Callable<Object>> defaultGroupValues, ICallback<Throwable> errorHandler, IMailer mailer, Callable<Long> timeGetter, IExtraReaderWriterConfigurator<ServerConfig> extraReaderWriterConfigurator) {
		super(port, workerThreads, timeOutMs, root, prefix, errorHandler, extraReaderWriterConfigurator);
		this.dataSource = dataSource;
		this.takeOnEnrichment = takeOnEnrichment;
		this.extraCallProcessors = extraCallProcessors;
		this.usage = usage;
		this.idAndSaltGenerator = idAndSaltGenerator;
		this.cryptoGenerators = cryptoGenerators;
		this.userCryptoAccess = userCryptoAccess;
		this.defaultGroupProperties = defaultGroupValues;
		this.mailer = mailer;
		this.timeGetter = timeGetter;
		this.defaultUserValues = Collections.unmodifiableMap(new HashMap<String, Callable<Object>>(defaultUserValues));
	}

}
