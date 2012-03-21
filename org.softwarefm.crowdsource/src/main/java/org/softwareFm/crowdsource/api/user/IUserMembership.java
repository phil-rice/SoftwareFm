/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.user;

public interface IUserMembership extends IUserMembershipReader {
	void addMembership(String softwareFmId, String userCrypto, String groupId, String groupCrypto, String membershipStatus);

	void setMembershipProperty(String softwareFmId, String userCrypto, String groupId,  String property, String value);

	/** removes the user. Note that we are passing the group crypto in case of corrupted data in the users files: we really want to remove this user! */
	void remove(String softwareFmId, String userCrypto, String groupId, String groupCrypto);

}