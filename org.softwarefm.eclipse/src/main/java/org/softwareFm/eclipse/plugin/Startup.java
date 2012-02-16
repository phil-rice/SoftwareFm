package org.softwareFm.eclipse.plugin;

import java.util.concurrent.Callable;

import org.eclipse.ui.IStartup;
import org.softwareFm.client.http.requests.IResponseCallback;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.services.IServiceExecutor;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.jdtBinding.BindingRipperResult;
import org.softwareFm.eclipse.snippets.internal.UsageStrategy;
import org.softwareFm.eclipse.usage.IUsageStrategy;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		final Activator activator = Activator.getDefault();
		final IServiceExecutor serviceExecutor = activator.getServiceExecutor();
		serviceExecutor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				IUsageStrategy rawUsageStrategy = new UsageStrategy(activator.getClient(), serviceExecutor, activator.getGitLocal(), LoginConstants.userGenerator(SoftwareFmConstants.urlPrefix));
				final IUsageStrategy cachedUsageStrategy = IUsageStrategy.Utils.cached(rawUsageStrategy, SoftwareFmConstants.usageRefreshTimeMs, activator.getUserDataManager());
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
				return null;
			}
		});
	}
}
