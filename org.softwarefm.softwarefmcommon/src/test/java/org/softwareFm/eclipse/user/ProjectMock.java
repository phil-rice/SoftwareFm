package org.softwareFm.eclipse.user;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

public class ProjectMock implements IProject {

	private final String expectedSoftwareFmId;
	private final String expectedProjectCryptoKey;
	private final Map<String, Object> map;


	public ProjectMock(String expectedSoftwareFmId, String expectedProjectCryptoKey, Map<String,Object> map) {
		this.expectedSoftwareFmId = expectedSoftwareFmId;
		this.expectedProjectCryptoKey = expectedProjectCryptoKey;
		this.map = map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Map<String, List<Integer>>> getProjectDetails(String softwareFmId, String projectCryptoKey, String month) {
		Assert.assertEquals(expectedSoftwareFmId, softwareFmId);
		Assert.assertEquals(expectedProjectCryptoKey, projectCryptoKey);
		return (Map<String, Map<String, List<Integer>>>) map.get(month);
	}

	@Override
	public void addProjectDetails(String softwareFmId, String groupId, String artifactId, String month, long day) {
		throw new UnsupportedOperationException();
	}

}
