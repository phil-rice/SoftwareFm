/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api;

import java.io.File;
import java.util.concurrent.Callable;

import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;

@SuppressWarnings("rawtypes")
public class ApiConfig {

	public final int port;
	public final File root;
	public final long timeOutMs;
	public final long staleCacheTimeMs;
	public final Callable<Long> timeGetter;
	public final IUrlGenerator userUrlGenerator;
	public final IUrlGenerator groupUrlGenerator;
	public final ICallback<Throwable> errorHandler;
	public final IExtraReaderWriterConfigurator extraReaderWriterConfigurator;
	public final int workerThreads;
	public final String prefix;

	public ApiConfig(int port, int workerThreads, long timeOutMs, long staleCacheTimeMs, File root, String urlPrefix, ICallback<Throwable> errorHandler, IExtraReaderWriterConfigurator extraReaderWriterConfigurator, Callable<Long> timeGetter) {
		this.port = port;
		this.workerThreads = workerThreads;
		this.timeOutMs = timeOutMs;
		this.staleCacheTimeMs = staleCacheTimeMs;
		this.root = root;
		this.prefix = urlPrefix;
		this.extraReaderWriterConfigurator = extraReaderWriterConfigurator;
		this.timeGetter = timeGetter;
		this.userUrlGenerator = LoginConstants.userGenerator(urlPrefix);
		this.groupUrlGenerator = GroupConstants.groupsGenerator(urlPrefix);
		this.errorHandler = errorHandler;
	}

}