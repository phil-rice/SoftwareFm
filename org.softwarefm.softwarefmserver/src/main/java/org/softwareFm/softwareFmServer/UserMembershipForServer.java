/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.softwareFmServer;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUser;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.crypto.Crypto;
import org.softwareFm.common.functions.Functions;
import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.json.Json;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.strings.Strings;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.user.AbstractUserMembershipReader;
import org.softwareFm.eclipse.user.IUserMembership;

public class UserMembershipForServer extends AbstractUserMembershipReader implements IUserMembership {

	private final IGitOperations gitOperations;
	private final IFunction1<String, String> repoDefnFn;
	private final IFunction1<Map<String, Object>, String> userCryptoFn;
	private final IUser user;

	public UserMembershipForServer(IUrlGenerator userUrlGenerator, IGitOperations gitOperations, IUser user, IFunction1<Map<String, Object>, String> userCryptoFn, IFunction1<String, String> repoDefnFn) {
		super(userUrlGenerator, user);
		this.user = user;
		this.gitOperations = gitOperations;
		this.userCryptoFn = userCryptoFn;
		this.repoDefnFn = repoDefnFn;
	}

	@Override
	public void addMembership(String softwareFmId, String groupId, String groupCrypto, String membershipStatus) {
		String usersMembershipCrypto = getMembershipCrypto(softwareFmId);

		for (Map<String, Object> map : walkGroupsFor(softwareFmId))
			if (groupId.equals(map.get(GroupConstants.groupIdKey)))
				throw new IllegalArgumentException(groupId);

		String url = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, GroupConstants.membershipFileName, usersMembershipCrypto);
		String text = getGroupFileAsText(fileDescription);
		Map<String, Object> map = Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCrypto, GroupConstants.membershipStatusKey, membershipStatus);
		String encrypted = Crypto.aesEncrypt(usersMembershipCrypto, Json.toString(map));
		File file = fileDescription.getFile(gitOperations.getRoot());
		if (!file.exists())
			Files.makeDirectoryForFile(file);

		if (fileDescription.findRepositoryUrl(gitOperations.getRoot()) == null) {
			String repositoryUrl = Functions.call(repoDefnFn, url);
			gitOperations.init(repositoryUrl);
		}
		Files.setText(file, text == null ? encrypted : text + "\n" + encrypted);
		String repositoryUrl = Files.offset(gitOperations.getRoot(), fileDescription.findRepositoryUrl(gitOperations.getRoot()));
		gitOperations.addAllAndCommit(repositoryUrl, "add membership " + groupId + "," + membershipStatus);
	}

	@Override
	public void setMembershipProperty(String softwareFmId, String groupId, String property, String value) {
		String url = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		String userMembershipCrypto = getMembershipCrypto(softwareFmId);
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, GroupConstants.membershipFileName, userMembershipCrypto);
		String text = getGroupFileAsText(fileDescription);
		List<String> lines = Strings.splitIgnoreBlanks(text, "\n");
		for (int i = 0; i < lines.size(); i++) {
			Map<String, Object> map = Json.mapFromString(Crypto.aesDecrypt(userMembershipCrypto, lines.get(i)));
			if (groupId.equals(map.get(GroupConstants.groupIdKey))) {
				Map<String, Object> newMap = Maps.with(map, property, value);
				StringBuilder builder = new StringBuilder();
				for (int j = 0; j < i; j++)
					builder.append(lines.get(j) + "\n");
				builder.append(Crypto.aesEncrypt(userMembershipCrypto, Json.toString(newMap)) + "\n");
				for (int j = i + 1; j < lines.size(); j++)
					builder.append(lines.get(j) + "\n");
				File file = fileDescription.getFile(gitOperations.getRoot());
				Files.setText(file, builder.toString());
				File findRepositoryFile = fileDescription.findRepositoryUrl(gitOperations.getRoot());
				String repositoryUrl = Files.offset(gitOperations.getRoot(), findRepositoryFile);
				if (repositoryUrl == null)
					throw new IllegalStateException(file.toString());
				gitOperations.addAllAndCommit(repositoryUrl, "setMembershipProperty(" + softwareFmId + ", " + groupId + "," + property);
				return;
			}
		}
		throw new IllegalArgumentException(groupId);
	}

	@Override
	protected String getGroupFileAsText(IFileDescription fileDescription) {
		return gitOperations.getFileAsString(fileDescription);
	}

	@Override
	protected String getMembershipCrypto(String softwareFmId) {
		String userCrypto = Functions.call(userCryptoFn, Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		String usersMembershipCrypto = user.getUserProperty(softwareFmId, userCrypto, GroupConstants.membershipCryptoKey);
		return usersMembershipCrypto;
	}
	
}