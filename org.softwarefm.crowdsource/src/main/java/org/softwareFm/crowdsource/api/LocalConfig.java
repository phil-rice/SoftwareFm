/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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