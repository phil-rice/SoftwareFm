/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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

	public static ServerConfig serverConfigForTests(File root, IMailer mailer) {
		ITakeOnEnrichmentProvider takeOnEnrichment = ITakeOnEnrichmentProvider.Utils.noEnrichment();
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		Map<String, Callable<Object>> defaultUserValues = Maps.makeMap(CommentConstants.commentCryptoKey, Callables.makeCryptoKey());
		Map<String, Callable<Object>> defaultGroupValues = Maps.makeMap(CommentConstants.commentCryptoKey, Callables.makeCryptoKey());
		IIdAndSaltGenerator idGenerators = IIdAndSaltGenerator.Utils.uuidGenerators();
		ICryptoGenerators cryptoGenerators = ICryptoGenerators.Utils.cryptoGenerators();
		IUserCryptoAccess userCryptoAccess = IUserCryptoAccess.Utils.database(dataSource, idGenerators);

		return new ServerConfig(CommonConstants.testPort, 10, CommonConstants.testTimeOutMs, CommonConstants.staleCachePeriodForTest, root, dataSource, takeOnEnrichment, IExtraCallProcessorFactory.Utils.noExtraCalls(), IUsage.Utils.noUsage(),//
				idGenerators, cryptoGenerators, userCryptoAccess, "testPrefix", defaultUserValues, defaultGroupValues, ICallback.Utils.rethrow(), mailer, //
				Callables.valueFromList(1000l, 2000l, 3000l, 4000l), IExtraReaderWriterConfigurator.Utils.<ServerConfig> noExtras());
	}

	public final Map<String, Callable<Object>> defaultUserValues;
	public final Map<String, Callable<Object>> defaultGroupProperties;
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

	public ServerConfig(int port, int workerThreads, long timeOutMs, long staleCacheTimeMs, File root, BasicDataSource dataSource, ITakeOnEnrichmentProvider takeOnEnrichment, IExtraCallProcessorFactory extraCallProcessors, IUsage usage, IIdAndSaltGenerator idAndSaltGenerator, ICryptoGenerators cryptoGenerators, IUserCryptoAccess userCryptoAccess, String prefix, Map<String, Callable<Object>> defaultUserValues, Map<String, Callable<Object>> defaultGroupProperties, ICallback<Throwable> errorHandler, IMailer mailer, Callable<Long> timeGetter, IExtraReaderWriterConfigurator<ServerConfig> extraReaderWriterConfigurator) {
		super(port, workerThreads, timeOutMs, staleCacheTimeMs, root, prefix, errorHandler, extraReaderWriterConfigurator, timeGetter);
		this.dataSource = dataSource;
		this.takeOnEnrichment = takeOnEnrichment;
		this.extraCallProcessors = extraCallProcessors;
		this.usage = usage;
		this.idAndSaltGenerator = idAndSaltGenerator;
		this.cryptoGenerators = cryptoGenerators;
		this.userCryptoAccess = userCryptoAccess;
		this.defaultGroupProperties = defaultGroupProperties;
		this.mailer = mailer;
		this.timeGetter = timeGetter;
		this.defaultUserValues = Collections.unmodifiableMap(new HashMap<String, Callable<Object>>(defaultUserValues));
	}
	
	@SuppressWarnings("unchecked")
	public ServerConfig withExtraProcessCalls(IExtraCallProcessorFactory extraCallProcessorFactory){
		return new ServerConfig(port, workerThreads, timeOutMs, staleCacheTimeMs, root, dataSource, takeOnEnrichment, extraCallProcessorFactory, usage, idAndSaltGenerator, cryptoGenerators, userCryptoAccess, prefix, defaultUserValues, defaultGroupProperties, errorHandler, mailer, timeGetter, extraReaderWriterConfigurator);
	}

}