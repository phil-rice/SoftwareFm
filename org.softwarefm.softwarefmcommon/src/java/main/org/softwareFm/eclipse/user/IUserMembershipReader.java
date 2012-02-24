/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.user;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.IGroupsReader;
import org.softwareFm.common.constants.GroupConstants;

public interface IUserMembershipReader {

	<T> T getMembershipProperty(String softwareFmId, String userCrypto, String groupId, String property);

	List<Map<String, Object>> walkGroupsFor(String softwareFmId, String crypto);

	public static class Utils {
		public static String findGroupProperty(IUserMembershipReader reader, IGroupsReader groupsReader, String softwareFmId, String crypto, String groupId, String property) {
			List<Map<String, Object>> groups = reader.walkGroupsFor(softwareFmId, crypto);
			for (Map<String, Object> map : groups) {
				if (groupId.equals(map.get(GroupConstants.groupIdKey))) {
					String groupCrypto = (String) map.get(GroupConstants.groupCryptoKey);
					return groupsReader.getGroupProperty(groupId, groupCrypto, property);
				}
			}
			throw new IllegalArgumentException("Could not find group with id: " + groupId + " in " + groups);
		}

		
	}

}