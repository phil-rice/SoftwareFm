/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.constants;

import org.softwareFm.common.url.IUrlGenerator;

public class GroupConstants {
	public static final long usageReportPeriod = 6 * 60 * 60 * 1000;// hrs
	public static final String myGroupsCardType = "myGroups";

	public static final String groupIdKey = "groupId";
	public static final String groupCryptoKey = "groupCrypto";
	public static final String groupNameKey = "groupName";
	public static final Object membershipStatusKey = "membershipStatus";
	public static final String membershipCryptoKey = "membershipCrypto";
	public static final String monthKey = "month";
	public static final String usageReportDirectory = "reports";
	public static final String takeOnCommandPrefix = "command/takeOnGroup";
	public static final String generateGroupReportPrefix = "command/generateGroupReport";

	public static final String takeOnEmailListKey = "emailList";
	public static final String takeOnFromKey = "from";
	public static final String takeOnSubjectKey = "subject";

	public static final String takeOnEmailPattern = "emailPattern";
	public static final String userStatusInGroup = "userStatus";

	public static final String emailMarker = "$email$";

	public static final String invitedStatus = "invited";
	public static final String membershipFileName = "membership.json";
	public static final String groupMembersTitle = "group.members.title";
	public static final String needToSelectGroup = "group.members.needToSelectGroup";
	public static final String groupIdNotFound = "Cannot find group id {0} for user {1}";

	public static IUrlGenerator groupsGenerator(String prefix) {
		return IUrlGenerator.Utils.generator(prefix + "/groups/{0}/{1}/{2}", groupIdKey);
	}

}