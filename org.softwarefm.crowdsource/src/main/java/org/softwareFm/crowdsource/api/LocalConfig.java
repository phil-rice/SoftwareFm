package org.softwareFm.crowdsource.api;

import java.io.File;

import org.softwareFm.crowdsource.api.git.IRepoFinder;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;

public class LocalConfig extends ApiConfig {

	public final String host;
	public final IRepoFinder repoFinder;
	public final String remoteGitPrefix;
	public final long timeOutMs;
	public final long autoRefreshPeriod;

	public LocalConfig(int port, int workerThreads, String host, File root, String urlPrefix, String remoteGitPrefix, long timeOutms, IRepoFinder repoFinder, long AutoRefreshPeriod, ICallback<Throwable> errorHandler, IExtraReaderWriterConfigurator extraReaderWriterConfigurator) {
		super(port, workerThreads, root, urlPrefix, errorHandler, extraReaderWriterConfigurator);
		this.host = host;
		this.remoteGitPrefix = remoteGitPrefix;
		this.timeOutMs = timeOutms;
		this.repoFinder = repoFinder;
		autoRefreshPeriod = AutoRefreshPeriod;
	}

}
