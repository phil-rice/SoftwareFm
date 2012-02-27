/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.softwareFmServer;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IGroups;
import org.softwareFm.common.IUser;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.runnable.Callables;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.IUserMembership;

public class TakeOnProcessor implements ITakeOnProcessor {

	private final IGitOperations gitOperations;
	private final IUser user;
	private final IGroups groups;
	private final IFunction1<Map<String, Object>, String> userCryptoFn;
	private final IUrlGenerator groupGenerator;
	private final Callable<String> groupIdGenerator;
	private final IFunction1<String, String> repoDefnFn;
	private final IUserMembership userMembership;

	public TakeOnProcessor(IGitOperations gitOperations, IUser user, IUserMembership userMembership, IGroups groups, IFunction1<Map<String, Object>, String> userCryptoFn, IUrlGenerator groupGenerator, Callable<String> groupIdGenerator, IFunction1<String, String> repoDefnFn) {
		this.gitOperations = gitOperations;
		this.user = user;
		this.userMembership = userMembership;
		this.groups = groups;
		this.userCryptoFn = userCryptoFn;
		this.groupGenerator = groupGenerator;
		this.groupIdGenerator = groupIdGenerator;
		this.repoDefnFn = repoDefnFn;
	}

	@Override
	public void addExistingUserToGroup(String groupId, String groupName, String groupCryptoKey, String softwareFmId, String email, String status) {
		String userCrypto = Functions.call(userCryptoFn, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId, LoginConstants.emailKey, email));
		String usersProjectCryptoKey = user.getUserProperty(softwareFmId, userCrypto, SoftwareFmConstants.projectCryptoKey);
		Map<String, Object> initialData = Maps.stringObjectMap(LoginConstants.emailKey, email, LoginConstants.softwareFmIdKey, softwareFmId, SoftwareFmConstants.projectCryptoKey, usersProjectCryptoKey, GroupConstants.membershipStatusKey, status);
		groups.addUser(groupId, groupCryptoKey, initialData);
		userMembership.addMembership(softwareFmId, userCrypto, groupId, groupCryptoKey, status);
	}

	@Override
	public String createGroup(String groupName, String groupCrypto) {
		String groupId = Callables.call(groupIdGenerator);
		String url = groupGenerator.findUrlFor(Maps.stringObjectMap(SoftwareFmConstants.groupIdKey, groupId));
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, groupCrypto);
		File repositoryFile = fileDescription.findRepositoryUrl(gitOperations.getRoot());
		if (repositoryFile == null) {
			String repositoryUrl = Functions.call(repoDefnFn, url);
			gitOperations.init(repositoryUrl);
			repositoryFile = new File(gitOperations.getRoot(), repositoryUrl);
		}
		File file = new File(gitOperations.getRoot(), Urls.compose(url, CommonConstants.dataFileName));
		if (file.exists())
			throw new IllegalStateException(file.toString());
		gitOperations.append(fileDescription, Maps.stringObjectMap(GroupConstants.groupNameKey, groupName));
		gitOperations.addAllAndCommit(Files.offset(gitOperations.getRoot(), repositoryFile), "createGroup (" + groupName + ")");
		return groupId;
	}

}