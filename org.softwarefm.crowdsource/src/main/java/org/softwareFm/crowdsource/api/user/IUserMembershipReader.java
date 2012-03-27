/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.api.user;

import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.crowdsource.api.IUserAndGroupsContainer;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.functions.IFunction2;

public interface IUserMembershipReader {

	<T> T getMembershipProperty(String softwareFmId, String userCrypto, String groupId, String property);

	Iterable<Map<String, Object>> walkGroupsFor(String softwareFmId, String crypto);

	public static class Utils {

		public static Iterable<Map<String, Object>> walkGroups(IUserAndGroupsContainer container, final String softwareFmId, final String crypto) {
			return container.accessUserMembershipReader(new IFunction2<IGroupsReader, IUserMembershipReader, Iterable<Map<String, Object>>>() {
				@Override
				public Iterable<Map<String, Object>> apply(IGroupsReader from1, IUserMembershipReader userMembershipReader) throws Exception {
					return userMembershipReader.walkGroupsFor(softwareFmId, crypto);
				}
			}, ICallback.Utils.<Iterable<Map<String, Object>>> noCallback()).get();
		}

		public static String findGroupProperty(IUserMembershipReader reader, IGroupsReader groupsReader, String softwareFmId, String crypto, String groupId, String property) {
			Iterable<Map<String, Object>> groups = reader.walkGroupsFor(softwareFmId, crypto);
			if (groups == null)
				throw new NullPointerException(MessageFormat.format(GroupConstants.notAMemberOfAnyGroup, softwareFmId));
			for (Map<String, Object> map : groups) {
				if (groupId.equals(map.get(GroupConstants.groupIdKey))) {
					String groupCrypto = (String) map.get(GroupConstants.groupCryptoKey);
					return groupsReader.getGroupProperty(groupId, groupCrypto, property);
				}
			}
			throw new IllegalArgumentException("Could not find group with id: " + groupId + " in " + groups);
		}

		public static String findGroupCrytpo(IUserMembershipReader reader, String softwareFmId, String userCrypto, String groupId) {
			if (userCrypto == null)
				throw new NullPointerException();
			Iterable<Map<String, Object>> groups = reader.walkGroupsFor(softwareFmId, userCrypto);
			if (groups == null)
				throw new NullPointerException(MessageFormat.format(GroupConstants.notAMemberOfAnyGroup, softwareFmId));
			for (Map<String, Object> map : groups)
				if (groupId.equals(map.get(GroupConstants.groupIdKey)))
					return (String) map.get(GroupConstants.groupCryptoKey);
			throw new IllegalArgumentException(MessageFormat.format(GroupConstants.notAMemberOfGroup, softwareFmId, groupId));
		}

	}

}