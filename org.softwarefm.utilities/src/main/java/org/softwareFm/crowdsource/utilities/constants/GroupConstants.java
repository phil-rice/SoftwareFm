/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.constants;

import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;

public class GroupConstants {
	public static final long usageReportPeriod = 6 * 60 * 60 * 1000;// hrs
	public static final String myGroupsCardType = "myGroups";

	public static final String objectSoftwareFmId= "objectSoftwareFmId"; //used when admin is doing things to other people
	public static final String groupIdKey = "groupId";
	public static final String groupCryptoKey = "groupCrypto";
	public static final String groupNameKey = "groupName";
	public static final String membershipStatusKey = "membershipStatus";
	public static final String membershipCryptoKey = "membershipCrypto";
	public static final String monthKey = "month";
	public static final String usageReportDirectory = "reports";
	public static final String takeOnCommandPrefix = "command/takeOnGroup";
	public static final String inviteCommandPrefix = "command/inviteGroup";
	public static final String acceptInvitePrefix = "command/acceptInvite";
	public static final String kickFromGroupPrefix = "command/kickFromGroup";
	public static final String leaveGroupPrefix = "command/leaveGroup";
	public static final String generateGroupReportPrefix = "command/generateGroupReport";

	public static final String takeOnEmailListKey = "emailList";
	public static final String takeOnFromKey = "from";
	public static final String takeOnSubjectKey = "subject";

	public static final String takeOnEmailPattern = "emailPattern";

	public static final String emailMarker = "$email$";
	public static final String groupNameMarker = "$group$";

	public static final String invitedStatus = "invited";
	public static final String memberStatus = "member";
	public static final String adminStatus = "admin";

	public static final String membershipFileName = "membership.json";
	public static final String groupMembersTitle = "group.members.title";
	public static final String needToSelectGroup = "group.members.needToSelectGroup";
	public static final String groupIdNotFound = "Cannot find group id {0} for user {1}";
	public static final String missingDataFromMembership = "Missing data from membership for user {0} map {1}";
	public static final String alreadyAMemberOfGroup = "Already a member of group. Sfm Id {0}. Group Id {1}";
	public static final String invalidEmail = "Invalid email {0}";
	public static final String emailSfmMismatch = "Email / SoftwareFm mismatch. Email {0} Expected {1} SoftwareFmId {2}";
	public static final String groupAlreadyExists = "Group already exists: {0}";
	public static final String takeOnEmailDefault = "takeOn.email.default";
	public static final String notAMemberOfGroup = "User {0} is not a member of group {1}";
	public static final String groupNameIsNull = "Group {0} has no name";
	public static final String cannotInviteToGroupUnlessAdmin = "Cannot invite other people to group {0} as you are not admin. You are status {1}";
	public static final String notAMemberOfAnyGroup = "This software fm id is not a member of any group {0}";
	public static final String errorSettingUserProperty = "Error setting user property. GroupId: {0} SoftwareFmId: {1} Property {2} Value {3} ChangedCount {4}";
	public static final String cannotKickUnlessAdmin = "Cannot kick unless admin.\n Group {0}\nUser {1}\nUsers status {2}\nObject SoftwareFmIds {3}";
	public static final String cannotKickAdmin = "Cannot kick admin.\nGroup {0}\nUser {1}\nUsers status {2}\nObject SoftwareFmId {3}";
	public static final String cannotLeaveIfAdminAndGroupIsntEmpty = "Cannot leave if admin and group isn't empty.\nGroup {0}\nUser {1}\nCount {2}";
	public static final String cannotDetermineGroupName = "Cannot work out name of group {0}, because of error {1}";

	public static IUrlGenerator groupsGenerator(String prefix) {
		return IUrlGenerator.Utils.generator(prefix + "/groups/{0}/{1}/{2}", groupIdKey);
	}

}