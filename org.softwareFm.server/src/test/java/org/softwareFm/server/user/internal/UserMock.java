package org.softwareFm.server.user.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.server.user.IUser;
import org.softwareFm.utilities.collections.Lists;

public class UserMock implements IUser{

	public final List<Map<String,Object>> userDetailMaps = Lists.newList();
	public final List<Map<String,Object>> datas = Lists.newList();
	public final AtomicInteger getUserDetailsCount = new AtomicInteger();
	@Override
	public Map<String, Object> getUserDetails(Map<String, Object> userDetailMap) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveUserDetails(Map<String, Object> userDetailMap, Map<String, Object> data) {
		getUserDetailsCount.incrementAndGet();
		userDetailMaps.add(userDetailMap);
		datas.add(data);
		
	}

	@Override
	public void saveProjectDetails(Map<String, Object> userDetailMap, String month, Map<String, Object> data) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getProjectDetails(Map<String, Object> userDetailMap, String month) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> addProjectDetails(Map<String, Object> userAndProjectDetailMap, String month, long day, Map<String, Object> initialProjectDetails) {
		throw new UnsupportedOperationException();
	}

}
