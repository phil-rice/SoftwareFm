/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.user;

import java.util.Collections;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitReader;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.url.IUrlGenerator;

public abstract class AbstractUserMembershipReader<GR extends IGitReader> implements IUserMembershipReader {

	protected final IUrlGenerator userUrlGenerator;
	protected final IUserReader user;
	protected final GR git;

	public AbstractUserMembershipReader(IUrlGenerator userUrlGenerator, IUserReader user, GR git) {
		this.userUrlGenerator = userUrlGenerator;
		this.user = user;
		this.git = git;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getMembershipProperty(String softwareFmId, String userCrypto, String groupId, String property) {
		for (Map<String, Object> membershipProperties : walkGroupsFor(softwareFmId, userCrypto))
			if (groupId.equals(membershipProperties.get(GroupConstants.groupIdKey)))
				return (T) membershipProperties.get(property);
		return null;
	}

	@Override
	public Iterable<Map<String, Object>> walkGroupsFor(String softwareFmId, String crypto) {
		String url = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		String usersMembershipCrypto = getMembershipCrypto(softwareFmId, crypto);
		if (usersMembershipCrypto== null)
			return Collections.emptyList();
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, GroupConstants.membershipFileName, usersMembershipCrypto);
		Iterable<Map<String, Object>> result =  git.getFileAsListOfMaps(fileDescription);
		return result;
	}

	protected String getMembershipCrypto(String softwareFmId ,String userCrypto) {
		String usersMembershipCrypto = user.getUserProperty(softwareFmId, userCrypto, GroupConstants.membershipCryptoKey);
		return usersMembershipCrypto;
	}

}