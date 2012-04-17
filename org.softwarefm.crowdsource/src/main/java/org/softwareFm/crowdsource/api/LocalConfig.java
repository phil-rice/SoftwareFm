package org.softwareFm.crowdsource.api;

import java.io.File;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;

public class LocalConfig extends ApiConfig {

	public final String host;
	public final String remoteGitPrefix;
	public final long autoRefreshPeriod;

	public LocalConfig(int port, int workerThreads, long timeOutMs, long staleCacheTimeMs, String host, File root, String urlPrefix, String remoteGitPrefix, long AutoRefreshPeriod, ICallback<Throwable> errorHandler, IExtraReaderWriterConfigurator<LocalConfig> extraReaderWriterConfigurator, Callable<Long> timeGetter) {
		super(port, workerThreads, timeOutMs, staleCacheTimeMs, root, urlPrefix, errorHandler, extraReaderWriterConfigurator, timeGetter);
		this.host = host;
		this.remoteGitPrefix = remoteGitPrefix;
		this.autoRefreshPeriod = AutoRefreshPeriod;
	}

}
