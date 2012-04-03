/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.plugin;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.ICrowdSourcedApi;
import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.httpClient.internal.IResponseCallback;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.maps.IHasCache;
import org.softwareFm.eclipse.jdtBinding.BindingRipperResult;
import org.softwareFm.jarAndClassPath.api.IUsageStrategy;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		final Activator activator = Activator.getDefault();
		new Thread() {
			@Override
			public void run() {
				ICrowdSourcedApi api = activator.getApi(display);
				IContainer container = api.makeContainer();
				IUsageStrategy rawUsageStrategy = IUsageStrategy.Utils.usage(container, activator.getLocalConfig().userUrlGenerator, CommonConstants.clientTimeOut);
				IHasCache gitLocal = container.access(IGitLocal.class, Functions.<IGitLocal, IGitLocal> identity()).get();
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
			}
		}.start();
	}
}