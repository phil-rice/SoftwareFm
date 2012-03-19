/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.plugin;

import org.eclipse.ui.IStartup;
import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.monitor.IMonitor;
import org.softwareFm.crowdsource.utilities.services.IServiceExecutor;
import org.softwareFm.eclipse.constants.EclipseMessages;
import org.softwareFm.eclipse.constants.JarAndPathConstants;
import org.softwareFm.eclipse.jdtBinding.BindingRipperResult;
import org.softwareFm.eclipse.snippets.internal.UsageStrategy;
import org.softwareFm.eclipse.usage.IUsageStrategy;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		final Activator activator = Activator.getDefault();
		final IServiceExecutor serviceExecutor = activator.getServiceExecutor();
		serviceExecutor.submit(new IFunction1<IMonitor, Void>() {
			@Override
			public Void apply(IMonitor monitor) throws Exception {
				monitor.beginTask(EclipseMessages.startUp, 1);
				try {
					IGitLocal gitLocal = activator.getGitLocal();
					IUsageStrategy rawUsageStrategy = new UsageStrategy(activator.getClient(), serviceExecutor, gitLocal, LoginConstants.userGenerator(JarAndPathConstants.urlPrefix));
					final IUsageStrategy cachedUsageStrategy = IUsageStrategy.Utils.cached(rawUsageStrategy, JarAndPathConstants.usageRefreshTimeMs, gitLocal, activator.getUserDataManager());
					activator.getSelectedBindingManager().addSelectedArtifactSelectionListener(new ISelectedBindingListener() {
						@Override
						public void selectionOccured(BindingRipperResult ripperResult) {
							if ("jar".equals(ripperResult.path.getFileExtension())) {
								String softwareFmId = activator.getUserDataManager().getUserData().softwareFmId;
								if (softwareFmId != null)
									cachedUsageStrategy.using(softwareFmId, ripperResult.hexDigest, IResponseCallback.Utils.noCallback());
							}
						}
					});
				} finally {
					monitor.done();
				}
				return null;
			}
		});
	}
}