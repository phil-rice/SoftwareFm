package org.softwareFm.eclipse.project;

import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.project.internal.ProjectForLocal;
import org.softwareFm.eclipse.user.IProjectReader;

public class UserAndProjectFactory {

	public static IProjectReader projectForLocal(IUserReader user, IUrlGenerator userUrlGenerator, String cryptoKey, IGitLocal gitLocal) {
		return new ProjectForLocal(user, userUrlGenerator, gitLocal, cryptoKey);
	}
}
