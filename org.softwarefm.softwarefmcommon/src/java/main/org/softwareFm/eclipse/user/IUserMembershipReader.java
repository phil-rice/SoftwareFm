package org.softwareFm.eclipse.user;

import java.util.List;
import java.util.Map;


public interface IUserMembershipReader {
	
	<T> T getMembershipProperty(String userId, String groupId,String property);
	
	List<Map<String,Object>> walkGroupsFor(String userId);

}
