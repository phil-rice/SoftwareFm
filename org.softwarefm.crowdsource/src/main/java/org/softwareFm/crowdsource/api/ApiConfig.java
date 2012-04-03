package org.softwareFm.crowdsource.api;

import java.io.File;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;

@SuppressWarnings("rawtypes")
public class ApiConfig {

	public final int port;
	public final File root;
	public final long timeOutMs;
	public final IUrlGenerator userUrlGenerator;
	public final IUrlGenerator groupUrlGenerator;
	public final ICallback<Throwable> errorHandler;
	public final IExtraReaderWriterConfigurator extraReaderWriterConfigurator;
	public final int workerThreads;
	public final String prefix;

	public ApiConfig(int port, int workerThreads, long timeOutMs, File root, String urlPrefix, ICallback<Throwable> errorHandler, IExtraReaderWriterConfigurator extraReaderWriterConfigurator) {
		this.port = port;
		this.workerThreads = workerThreads;
		this.timeOutMs = timeOutMs;
		this.root = root;
		this.prefix = urlPrefix;
		this.extraReaderWriterConfigurator = extraReaderWriterConfigurator;
		this.userUrlGenerator = LoginConstants.userGenerator(urlPrefix);
		this.groupUrlGenerator = GroupConstants.groupsGenerator(urlPrefix);
		this.errorHandler = errorHandler;
	}

}
