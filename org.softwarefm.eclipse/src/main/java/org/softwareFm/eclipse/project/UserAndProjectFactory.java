package org.softwareFm.eclipse.project;

import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.project.internal.UsageReaderForLocal;
import org.softwareFm.eclipse.user.IUsageReader;

public class UserAndProjectFactory {

	public static IUsageReader projectForLocal(IUserReader user, IUrlGenerator userUrlGenerator, String cryptoKey, IGitLocal gitLocal) {
		return new UsageReaderForLocal(user, userUrlGenerator, gitLocal, cryptoKey);
	}
}
