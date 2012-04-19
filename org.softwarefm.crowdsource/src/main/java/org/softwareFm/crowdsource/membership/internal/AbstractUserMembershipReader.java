/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.membership.internal;

import java.util.Collections;
import java.util.Map;

import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitReader;
import org.softwareFm.crowdsource.api.user.IUserMembershipReader;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;

public abstract class AbstractUserMembershipReader implements IUserMembershipReader {

	protected final IUserAndGroupsContainer container;
	protected final IUrlGenerator userUrlGenerator;

	public AbstractUserMembershipReader(IUserAndGroupsContainer container, IUrlGenerator userUrlGenerator) {
		this.container = container;
		this.userUrlGenerator = userUrlGenerator;
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
		if (usersMembershipCrypto == null)
			return Collections.emptyList();
		final IFileDescription fileDescription = IFileDescription.Utils.encrypted(url, GroupConstants.membershipFileName, usersMembershipCrypto);
		return IGitReader.Utils.getFileAsListOfMaps(container, fileDescription);
	}

	protected String getMembershipCrypto(String softwareFmId, String userCrypto) {
		String usersMembershipCrypto = IUserReader.Utils.getUserProperty(container, softwareFmId, userCrypto, GroupConstants.membershipCryptoKey);
		return usersMembershipCrypto;
	}

}