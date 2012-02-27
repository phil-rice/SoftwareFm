/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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
import org.softwareFm.eclipse.constants.CommentConstants;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IProject;
import org.softwareFm.eclipse.user.IProjectTimeGetter;
import org.softwareFm.eclipse.user.IUsageReader;
import org.softwareFm.eclipse.user.IUserMembership;
import org.softwareFm.server.ICrowdSourcedServer;
import org.softwareFm.server.comments.CommentsForServer;
import org.softwareFm.server.comments.IComments;
import org.softwareFm.server.processors.CommentProcessor;
import org.softwareFm.server.processors.IGenerateUsageReportGenerator;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.ProcessCallParameters;

public class SoftwareFmServer {

	public static void main(String[] args) {
		DOMConfigurator.configure(System.getProperty("user.home") + "/log4j.xml");
		int port = ICrowdSourcedServer.Utils.port(args);

		makeSoftwareFmServer(port);
	}

	public static void makeSoftwareFmServer(int port) {
		BasicDataSource dataSource = AbstractLoginDataAccessor.defaultDataSource();
		IGitOperations gitOperations = IGitOperations.Utils.gitOperations(ICrowdSourcedServer.Utils.makeSfmRoot());
		ICrowdSourcedServer.Utils.fullServer(port, gitOperations, dataSource, makeExtraProcessCalls(), SoftwareFmConstants.urlPrefix, makeDefaultProperties());
	}

	private static IFunction1<ProcessCallParameters, IProcessCall[]> makeExtraProcessCalls() {
		return new IFunction1<ProcessCallParameters, IProcessCall[]>() {
			@Override
			public IProcessCall[] apply(final ProcessCallParameters processCallParameters) throws Exception {
				IUrlGenerator groupsUrlGenerator = GroupConstants.groupsGenerator(SoftwareFmConstants.urlPrefix);
				IUrlGenerator userUrlGenerator = LoginConstants.userGenerator(SoftwareFmConstants.urlPrefix);
				Callable<String> groupIdGenerator = Callables.uuidGenerator();
				IFunction1<String, String> repoDefnFn = Strings.firstNSegments(3);
				IGroups groups = new GroupsForServer(groupsUrlGenerator, processCallParameters.gitOperations, repoDefnFn);
				IFunction1<String, String> emailToSoftwareFmId = ICrowdSourcedServer.Utils.emailToSoftwareFmId(processCallParameters.dataSource);
				IUserMembership userMembership = new UserMembershipForServer(userUrlGenerator, processCallParameters.user, processCallParameters.gitOperations, repoDefnFn);
				ITakeOnProcessor takeOnProcessor = new TakeOnProcessor(processCallParameters.gitOperations, processCallParameters.user, userMembership, groups, processCallParameters.userCryptoFn, groupsUrlGenerator, groupIdGenerator, repoDefnFn);

				IUsageReader usageReader = new UsageReaderForServer(processCallParameters.gitOperations, processCallParameters.user, userUrlGenerator);
				IGenerateUsageReportGenerator generator = new GenerateUsageProjectGenerator(groups, usageReader);
				IComments comments = new CommentsForServer(processCallParameters.gitOperations, processCallParameters.user, userMembership, groups, Callables.time());
				return new IProcessCall[] { makeUsageProcessor(processCallParameters.dataSource, processCallParameters.gitOperations, processCallParameters.user, userUrlGenerator),//
						new CommentProcessor(processCallParameters.user, userMembership, groups, comments, processCallParameters.userCryptoFn),//
						new GenerateGroupUsageProcessor(processCallParameters.gitOperations, generator, groups),//
						new TakeOnGroupProcessor(takeOnProcessor, processCallParameters.signUpChecker, Callables.makeCryptoKey(), emailToSoftwareFmId, processCallParameters.saltGenerator, processCallParameters.softwareFmIdGenerator, processCallParameters.mailer) };
			}
		};
	}

	protected static UsageProcessor makeUsageProcessor(BasicDataSource dataSource, IGitOperations gitOperations, IUser user, IUrlGenerator userUrlGenerator) {
		IProject project = new ProjectForServer(gitOperations, ICrowdSourcedServer.Utils.cryptoFn(dataSource), user, userUrlGenerator);
		IProjectTimeGetter projectTimeGetter = IProjectTimeGetter.Utils.timeGetter();
		UsageProcessor usageProcessor = new UsageProcessor(gitOperations, new JarToGroupArtifactVersionOnServer(SoftwareFmConstants.jarUrlGenerator(SoftwareFmConstants.urlPrefix), gitOperations), project, projectTimeGetter);
		return usageProcessor;
	}

	public static Map<String, Callable<Object>> makeDefaultProperties() {
		return Maps.makeMap(GroupConstants.membershipCryptoKey, Callables.makeCryptoKey(), //
				GroupConstants.groupCryptoKey, Callables.makeCryptoKey(),//
				CommentConstants.commentCryptoKey, Callables.makeCryptoKey(),//
				SoftwareFmConstants.projectCryptoKey, Callables.makeCryptoKey());
	}

	public static IUser makeUser(IGitOperations gitOperations, IUrlGenerator userUrlGenerator, Map<String, Callable<Object>> defaultProperties) {
		IUser user = ICrowdSourcedServer.Utils.makeUserForServer(gitOperations, userUrlGenerator, Strings.firstNSegments(3), defaultProperties);
		return user;

	}

}