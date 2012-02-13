package org.softwareFm.common.constants;

import org.softwareFm.common.url.IUrlGenerator;

public class GroupConstants {
	public static final long usageReportPeriod = 6*60*60*1000;//hrs
	public static final String myGroupsCardType = "myGroups";

	public static final String groupIdKey = "groupId";
	public static final String groupCryptoKey = "groupCrypto";
	public static final String groupNameKey = "groupName";
	public static final Object membershipStatusKey = "membershipStatus";
	public static final String membershipCryptoKey = "membershipCrypto";
	public static final String monthKey = "month";
	public static final String usageReportDirectory = "reports";
	public static final String takeOnCommandPrefix = "command/takeOnGroup";
	public static final String generateGroupReportPrefix = "command/generateGroupReport" ;
	
	
	public static final String takeOnEmailListKey = "emailList";
	public static final String takeOnFromKey = "from";
	public static final String takeOnSubjectKey = "subject";
	
	public static final String takeOnEmailPattern = "emailPattern";
	public static final String userStatusInGroup = "userStatus";
	
	public static final String emailMarker = "$email$";

	public static final String invitedStatus = "invited";
	public static final String membershipFileName = "membership.json";

	public static IUrlGenerator groupsGenerator() {
		return IUrlGenerator.Utils.generator("softwareFm/groups/{0}/{1}/{2}", groupIdKey);
	}

}
