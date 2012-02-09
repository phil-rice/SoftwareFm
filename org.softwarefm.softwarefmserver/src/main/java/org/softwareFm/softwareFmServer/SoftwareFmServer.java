package org.softwareFm.softwareFmServer;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.xml.DOMConfigurator;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.user.IProject;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.server.ICrowdSourcedServer;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.ProcessCallParameters;

public class SoftwareFmServer {

	public static void main(String[] args) {
		DOMConfigurator.configure(System.getProperty("user.home") + "/log4j.xml");

		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		IGitOperations gitOperations = IGitOperations.Utils.gitOperations(ICrowdSourcedServer.Utils.makeSfmRoot());
		ICrowdSourcedServer.Utils.fullServer(gitOperations, dataSource, makeExtraProcessCalls());
	}

	private static IFunction1<ProcessCallParameters, IProcessCall[]> makeExtraProcessCalls() {
		return new IFunction1<ProcessCallParameters, IProcessCall[]>() {
			@Override
			public IProcessCall[] apply(ProcessCallParameters from) throws Exception {
				return new IProcessCall[] {makeUsageProcessor(from.dataSource, from.gitOperations)};
			}
		};
	}

	protected static UsageProcessor makeUsageProcessor(BasicDataSource dataSource, IGitOperations gitOperations) {
		IUrlGenerator userUrlGenerator = LoginConstants.userGenerator();
		IUser user = ICrowdSourcedServer.Utils.makeUserForServer(gitOperations, userUrlGenerator, Strings.firstNSegments(3));
		IProject project = new ProjectForServer(gitOperations, ICrowdSourcedServer.Utils.cryptoFn(dataSource), user, userUrlGenerator, Crypto.makeKeyCallable());
		IProjectTimeGetter projectTimeGetter = IProjectTimeGetter.Utils.timeGetter();
		UsageProcessor usageProcessor = new UsageProcessor(gitOperations, project, projectTimeGetter);
		return usageProcessor;
	}

}
