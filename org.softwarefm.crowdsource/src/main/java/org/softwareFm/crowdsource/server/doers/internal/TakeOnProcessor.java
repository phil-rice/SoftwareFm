/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.server.doers.internal;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.ServerConfig;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.server.ITakeOnProcessor;
import org.softwareFm.crowdsource.api.user.IGroups;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback2;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.Urls;

public class TakeOnProcessor implements ITakeOnProcessor {

	private final ServerConfig serverConfig;
	private final IUserAndGroupsContainer container;

	public TakeOnProcessor(IUserAndGroupsContainer container, ServerConfig serverConfig) {
		this.container = container;
		this.serverConfig = serverConfig;
	}

	@Override
	public void addExistingUserToGroup(final String groupId, final String groupCryptoKey, final String softwareFmId, final String email, final String status) {
		container.accessUserMembership(new ICallback2<IGroups, IUserMembership>() {
			@Override
			public void process(IGroups groups, IUserMembership userMembership) throws Exception {
				String userCrypto = serverConfig.userCryptoAccess.getCryptoForUser(softwareFmId);
				if (userCrypto == null)
					throw new IllegalStateException(MessageFormat.format("Cannot add existing user {0} to group {1} as cannot determine usercrypto", softwareFmId, groupId));
				Map<String, Object> initialData = Maps.stringObjectMap(LoginConstants.emailKey, email, LoginConstants.softwareFmIdKey, softwareFmId, GroupConstants.membershipStatusKey, status);
				Map<String, Object> enrichedData = serverConfig.takeOnEnrichment.takeOn(initialData, userCrypto, container);
				groups.addUser(groupId, groupCryptoKey, enrichedData);
				userMembership.addMembership(softwareFmId, userCrypto, groupId, groupCryptoKey, status);
			}
		}).get();
	}

	@Override
	public String createGroup(String groupName, String groupCrypto) {
		IGitOperations gitOperations = container.gitOperations();
		String groupId = serverConfig.idAndSaltGenerator.makeNewGroupId();
		String url = serverConfig.groupUrlGenerator.findUrlFor(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId));
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, CommonConstants.dataFileName, groupCrypto);
		File repositoryFile = fileDescription.findRepositoryUrl(gitOperations.getRoot());
		if (repositoryFile == null) {
			String repositoryUrl = Functions.call(serverConfig.groupRepoDefnFn, url);
			gitOperations.init(repositoryUrl);
			repositoryFile = new File(gitOperations.getRoot(), repositoryUrl);
		}
		File file = new File(gitOperations.getRoot(), Urls.compose(url, CommonConstants.dataFileName));
		if (file.exists())
			throw new IllegalStateException(MessageFormat.format(GroupConstants.groupAlreadyExists, file));
		gitOperations.append(fileDescription, Maps.stringObjectMap(GroupConstants.groupNameKey, groupName));
		gitOperations.addAllAndCommit(Files.offset(gitOperations.getRoot(), repositoryFile), "createGroup (" + groupName + ")");
		return groupId;
	}

}