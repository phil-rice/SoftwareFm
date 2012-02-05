package org.softwareFm.softwareFmServer;

import org.apache.commons.dbcp.BasicDataSource;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.user.IProject;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.server.ICrowdSourcedServer;

public class SoftwareFmServer {

	public static void main(String[] args) {
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		IGitOperations gitOperations = IGitOperations.Utils.gitOperations(ICrowdSourcedServer.Utils.makeSfmRoot());
		ICrowdSourcedServer.Utils.fullServer(gitOperations, dataSource, //
				makeUsageProcessor(dataSource, gitOperations));
	}

	protected static UsageProcessor makeUsageProcessor(BasicDataSource dataSource, IGitOperations gitOperations) {
		IUrlGenerator userUrlGenerator = LoginConstants.userGenerator();
		IUser user = IUser.Utils.makeUserForServer(gitOperations, userUrlGenerator, Strings.firstNSegments(3));
		IProject project = new ProjectForServer(gitOperations, ICrowdSourcedServer.Utils.cryptoFn(dataSource), user, userUrlGenerator, Crypto.makeKeyCallable());
		IProjectTimeGetter projectTimeGetter = IProjectTimeGetter.Utils.timeGetter();
		UsageProcessor usageProcessor = new UsageProcessor(gitOperations, project, projectTimeGetter);
		return usageProcessor;
	}

}
