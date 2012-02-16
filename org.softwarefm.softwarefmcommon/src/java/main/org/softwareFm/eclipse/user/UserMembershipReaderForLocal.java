/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.user;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.constants.GroupConstants;
import org.softwareFm.common.url.IUrlGenerator;

public class UserMembershipReaderForLocal extends AbstractUserMembershipReader {

	private final IGitLocal gitLocal;
	private final String userCryptoKey;

	public UserMembershipReaderForLocal(IUrlGenerator userUrlGenerator, IGitLocal gitLocal, IUserReader user, String userCryptoKey) {
		super(userUrlGenerator, user);
		this.gitLocal = gitLocal;
		this.userCryptoKey = userCryptoKey;
	}

	@Override
	protected String getGroupFileAsText(IFileDescription fileDescription) {
		return gitLocal.getFileAsString(fileDescription);
	}

	@Override
	protected String getMembershipCrypto(String softwareFmId) {
		String result = user.getUserProperty(softwareFmId, userCryptoKey, GroupConstants.membershipCryptoKey);
		return result;
	}

}