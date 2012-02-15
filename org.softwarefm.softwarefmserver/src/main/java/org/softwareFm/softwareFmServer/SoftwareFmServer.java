package org.softwareFm.softwareFmServer;

import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.xml.DOMConfigurator;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IGroups;
import org.softwareFm.common.IUser;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.processors.AbstractLoginDataAccessor;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IProject;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.eclipse.user.IUsageReader;
import org.softwareFm.eclipse.user.IUserMembership;
import org.softwareFm.server.ICrowdSourcedServer;
import org.softwareFm.server.processors.IGenerateUsageReportGenerator;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.ProcessCallParameters;

public class SoftwareFmServer {

	public static void main(String[] args) {
		DOMConfigurator.configure(System.getProperty("user.home") + "/log4j.xml");

		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		IGitOperations gitOperations = IGitOperations.Utils.gitOperations(ICrowdSourcedServer.Utils.makeSfmRoot());
		ICrowdSourcedServer.Utils.fullServer(ICrowdSourcedServer.Utils.port(args), gitOperations, dataSource, makeExtraProcessCalls());
	}

	private static IFunction1<ProcessCallParameters, IProcessCall[]> makeExtraProcessCalls() {
		return new IFunction1<ProcessCallParameters, IProcessCall[]>() {
			@Override
			public IProcessCall[] apply(final ProcessCallParameters processCallParameters) throws Exception {
				Callable<String> groupIdGenerator = Callables.uuidGenerator();
				IFunction1<String, String> repoDefnFn = Strings.firstNSegments(3);
				IGroups groups = new GroupsForServer(GroupConstants.groupsGenerator(), processCallParameters.gitOperations, repoDefnFn);
				IFunction1<String, String> emailToSoftwareFmId = ICrowdSourcedServer.Utils.emailToSoftwareFmId(processCallParameters.dataSource);
				IUserMembership userMembership = new UserMembershipForServer(LoginConstants.userGenerator(), processCallParameters.gitOperations, processCallParameters.user, processCallParameters.userCryptoFn, repoDefnFn);
				ITakeOnProcessor takeOnProcessor = new TakeOnProcessor(processCallParameters.gitOperations, processCallParameters.user, userMembership, groups, processCallParameters.userCryptoFn, emailToSoftwareFmId, GroupConstants.groupsGenerator(), groupIdGenerator, repoDefnFn);

				IUsageReader usageReader = new UsageReaderForServer(processCallParameters.gitOperations, processCallParameters.user, LoginConstants.userGenerator());
				IGenerateUsageReportGenerator generator = new GenerateUsageProjectGenerator(groups, usageReader);
				return new IProcessCall[] { makeUsageProcessor(processCallParameters.dataSource, processCallParameters.gitOperations),//
						new GenerateGroupUsageProcessor(processCallParameters.gitOperations, generator, groups),//
						new TakeOnGroupProcessor(takeOnProcessor, processCallParameters.signUpChecker, Callables.makeCryptoKey(), emailToSoftwareFmId, processCallParameters.saltGenerator, processCallParameters.softwareFmIdGenerator, processCallParameters.mailer) };
			}
		};
	}

	protected static UsageProcessor makeUsageProcessor(BasicDataSource dataSource, IGitOperations gitOperations) {
		IUrlGenerator userUrlGenerator = LoginConstants.userGenerator();
		IUser user = makeUser(gitOperations, userUrlGenerator);
		IProject project = new ProjectForServer(gitOperations, ICrowdSourcedServer.Utils.cryptoFn(dataSource), user, userUrlGenerator);
		IProjectTimeGetter projectTimeGetter = IProjectTimeGetter.Utils.timeGetter();
		UsageProcessor usageProcessor = new UsageProcessor(gitOperations, project, projectTimeGetter);
		return usageProcessor;
	}

	public static Map<String, Callable<Object>> makeDefaultProperties() {
		return Maps.makeMap(GroupConstants.membershipCryptoKey, Callables.makeCryptoKey(), //
				GroupConstants.groupCryptoKey, Callables.makeCryptoKey(),//
				SoftwareFmConstants.projectCryptoKey, Callables.makeCryptoKey());
	}

	public static IUser makeUser(IGitOperations gitOperations, IUrlGenerator userUrlGenerator, Map<String, Callable<Object>> defaultProperties) {
		IUser user = ICrowdSourcedServer.Utils.makeUserForServer(gitOperations, userUrlGenerator, Strings.firstNSegments(3), defaultProperties);
		return user;

	}

	public static IUser makeUser(IGitOperations gitOperations, IUrlGenerator userUrlGenerator) {
		return makeUser(gitOperations, userUrlGenerator, makeDefaultProperties());
	}
}