package org.softwareFm.crowdsource.api;

import java.io.File;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;

public class LocalConfig extends ApiConfig {

	public final String host;
	public final String remoteGitPrefix;
	public final long autoRefreshPeriod;

	public LocalConfig(int port, int workerThreads, long timeOutMs, String host, File root, String urlPrefix, String remoteGitPrefix, long AutoRefreshPeriod, ICallback<Throwable> errorHandler, IExtraReaderWriterConfigurator<LocalConfig> extraReaderWriterConfigurator) {
		super(port, workerThreads, timeOutMs, root, urlPrefix, errorHandler, extraReaderWriterConfigurator);
		this.host = host;
		this.remoteGitPrefix = remoteGitPrefix;
		this.autoRefreshPeriod = AutoRefreshPeriod;
	}

}
