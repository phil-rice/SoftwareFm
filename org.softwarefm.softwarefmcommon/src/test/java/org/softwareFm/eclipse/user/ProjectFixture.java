package org.softwareFm.eclipse.user;

import java.util.Arrays;
import java.util.Map;

import org.softwareFm.common.maps.Maps;

public class ProjectFixture {
	private final static Map<String, Object> map1 = Maps.stringObjectMap(//
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

	private final static Map<String, Object> map2 = Maps.stringObjectMap(//
			"month1", Maps.stringObjectMap(//
					"group1", Maps.stringObjectMap(//
							"artifact11", Arrays.asList(1, 3), "artifact12", Arrays.asList(1, 2, 3)),//
					"group2", Maps.stringObjectMap(//
							"artifact21", Arrays.asList(1, 2), "artifact22", Arrays.asList(1, 2, 3))),//
			"month3", Maps.stringObjectMap(//
					"group1", Maps.stringObjectMap(//
							"artifact11", Arrays.asList(1), "artifact12", Arrays.asList(1, 2, 3)),//
					"group3", Maps.stringObjectMap(//
							"artifact31", Arrays.asList(1, 2), "artifact32", Arrays.asList(4, 5))));

	public final static Map<String, Object> expectedMergeResultMonth1 = Maps.stringObjectMap(//
			"group1", Maps.stringObjectMap(//
					"artifact11", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 3l, 4l, 5l), "sfm2", Arrays.asList(1l, 3l)),//
					"artifact12", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l, 3l), "sfm2", Arrays.asList(1l, 2l, 3l))),//
			"group2", Maps.stringObjectMap(//
					"artifact21", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l), "sfm2", Arrays.asList(1l, 2l)), //
					"artifact22", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l, 3l), "sfm2", Arrays.asList(1l, 2l, 3l))));
	public final static Map<String, Object> expectedMergeResultMonth2 = Maps.stringObjectMap(//
			"group1", Maps.stringObjectMap(//
					"artifact11", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 3l, 4l, 5l)),//
					"artifact13", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l, 3l))),//
			"group2", Maps.stringObjectMap(//
					"artifact21", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l)), //
					"artifact22", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l, 3l))));

	public static final Map<String, Object> expectedMergeResultMonth3 = Maps.stringObjectMap(//
			"group1", Maps.stringObjectMap(//
					"artifact11", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 3l, 4l, 5l), "sfm2", Arrays.asList(1l)),//
					"artifact12", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l, 3l), "sfm2", Arrays.asList(1l, 2l, 3l))),//
			"group3", Maps.stringObjectMap(//
					"artifact31", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l), "sfm2", Arrays.asList(1l, 2l)), //
					"artifact32", Maps.stringObjectMap("sfm1", Arrays.asList(1l, 2l, 3l), "sfm2", Arrays.asList(4l, 5l))));

	public static ProjectMock project1(String expectedSoftwareFmId, String expectedProjectCryptoKey) {
		return new ProjectMock(expectedSoftwareFmId, expectedProjectCryptoKey, map1);
	}

	public static ProjectMock project2(String expectedSoftwareFmId, String expectedProjectCryptoKey) {
		return new ProjectMock(expectedSoftwareFmId, expectedProjectCryptoKey, map2);
	}
}
