package org.softwareFm.common.constants;

import org.softwareFm.common.url.IUrlGenerator;

public class GroupConstants {

	public static final String groupIdKey = "groupId";
	public static final String usageReportDirectory = "reports";
	public static final String takeOnCommandPrefix = "command/takeOnGroup";
	public static final Object groupNameKey = "groupName";
	public static final Object takeOnEmailListKey = "emailList";
	public static final Object takeOnEmailPattern = "";

	public static IUrlGenerator groupsGenerator() {
		return IUrlGenerator.Utils.generator("softwareFm/groups/{0}/{1}/{2}", groupIdKey);
	}

}
