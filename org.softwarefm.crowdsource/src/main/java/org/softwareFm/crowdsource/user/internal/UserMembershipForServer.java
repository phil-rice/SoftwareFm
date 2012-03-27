/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.user.internal;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.git.IGitWriter;
import org.softwareFm.crowdsource.api.user.IUserMembership;
import org.softwareFm.crowdsource.membership.internal.AbstractUserMembershipReader;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.collections.Files;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.functions.Functions;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;

public class UserMembershipForServer extends AbstractUserMembershipReader implements IUserMembership {

	private final IFunction1<String, String> repoDefnFn;
	private final IGitOperations git;

	public UserMembershipForServer(IUserAndGroupsContainer container, IUrlGenerator userUrlGenerator, IFunction1<String, String> repoDefnFn) {
		super(container, userUrlGenerator);
		this.git = container.gitOperations();
		this.repoDefnFn = repoDefnFn;
	}

	@Override
	public void addMembership(final String softwareFmId, final String userCrypto, final String groupId, final String groupCrypto, final String membershipStatus) {
		container.access( IGitWriter.class, new ICallback< IGitWriter>() {
			@Override
			public void process( IGitWriter writer) throws Exception {
				String usersMembershipCrypto = getMembershipCrypto(softwareFmId, userCrypto);
				File root = writer.getRoot();

				for (Map<String, Object> map : walkGroupsFor(softwareFmId, userCrypto))
					if (groupId.equals(map.get(GroupConstants.groupIdKey)))
						throw new IllegalArgumentException(MessageFormat.format(GroupConstants.alreadyAMemberOfGroup, softwareFmId, groupId));

				String url = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
				IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, GroupConstants.membershipFileName, usersMembershipCrypto);
				if (fileDescription.findRepositoryUrl(root) == null) {
					String repositoryUrl = Functions.call(repoDefnFn, url);
					writer.init(repositoryUrl, "Init - adding membership");
				}
				Map<String, Object> data = Maps.stringObjectMap(GroupConstants.groupIdKey, groupId, GroupConstants.groupCryptoKey, groupCrypto, GroupConstants.membershipStatusKey, membershipStatus);
				String repositoryUrl = Files.offset(root, fileDescription.findRepositoryUrl(root));
				container.gitOperations().append(fileDescription, data);
				container.gitOperations().addAllAndCommit(repositoryUrl, "add membership " + groupId + "," + membershipStatus);
			}
		}).get();
	}

	@Override
	public void setMembershipProperty(String softwareFmId, String userCrypto, final String groupId, final String property, final String value) {
		String url = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		String userMembershipCrypto = getMembershipCrypto(softwareFmId, userCrypto);
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, GroupConstants.membershipFileName, userMembershipCrypto);
		int count = git.map(fileDescription, new IFunction1<Map<String, Object>, Boolean>() {
			@Override
			public Boolean apply(Map<String, Object> from) throws Exception {
				boolean result = groupId.equals(from.get(GroupConstants.groupIdKey));
				return result;
			}
		}, new IFunction1<Map<String, Object>, Map<String, Object>>() {

			@Override
			public Map<String, Object> apply(Map<String, Object> from) throws Exception {
				Map<String, Object> newMap = Maps.with(from, property, value);
				return newMap;
			}
		}, "setMembershipProperty(" + softwareFmId + ", " + groupId + "," + property);
		if (count == 0)
			throw new IllegalArgumentException(MessageFormat.format(GroupConstants.groupIdNotFound, groupId, softwareFmId));
	}

	@Override
	public void remove(final String softwareFmId, String userCrypto, final String groupId, String groupCrypto) {
		String url = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		String userMembershipCrypto = getMembershipCrypto(softwareFmId, userCrypto);
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, GroupConstants.membershipFileName, userMembershipCrypto);
		git.removeLine(fileDescription, new IFunction1<Map<String, Object>, Boolean>() {
			@Override
			public Boolean apply(Map<String, Object> from) throws Exception {
				return groupId.equals(from.get(GroupConstants.groupIdKey));
			}
		}, "removingUser(" + softwareFmId + ")");
	}

}