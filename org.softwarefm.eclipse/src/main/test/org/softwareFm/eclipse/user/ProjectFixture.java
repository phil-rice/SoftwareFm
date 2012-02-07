package org.softwareFm.eclipse.user;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.softwareFm.common.maps.Maps;

public class ProjectFixture implements IProject {

	private final Map<String, Object> map = Maps.stringObjectMap(//
			"month1", Maps.stringObjectMap(//
					"group1", Maps.stringObjectMap(//
							"artifact11", Arrays.asList(1, 3, 4, 5), "artifact12", Arrays.asList(1, 2, 3)),//
					"group2", Maps.stringObjectMap(//
							"artifact21", Arrays.asList(1, 2), "artifact22", Arrays.asList(1, 2, 3))),//
			"month2", Maps.stringObjectMap(//
					"group1", Maps.stringObjectMap(//
							"artifact11", Arrays.asList(1, 3, 4, 5), "artifact13", Arrays.asList(1, 2, 3)),//
					"group2", Maps.stringObjectMap(//
							"artifact21", Arrays.asList(1, 2), "artifact22", Arrays.asList(1, 2, 3))),//
			"month3", Maps.stringObjectMap(//
					"group1", Maps.stringObjectMap(//
							"artifact11", Arrays.asList(1, 3, 4, 5), "artifact12", Arrays.asList(1, 2, 3)),//
					"group3", Maps.stringObjectMap(//
							"artifact31", Arrays.asList(1, 2), "artifact32", Arrays.asList(1, 2, 3))));
	private final String expectedSoftwareFmId;
	private final String expectedProjectCryptoKey;


	public ProjectFixture(String expectedSoftwareFmId, String expectedProjectCryptoKey) {
		this.expectedSoftwareFmId = expectedSoftwareFmId;
		this.expectedProjectCryptoKey = expectedProjectCryptoKey;
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
