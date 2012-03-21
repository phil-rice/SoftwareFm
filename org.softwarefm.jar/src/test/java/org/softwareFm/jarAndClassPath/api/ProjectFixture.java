/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.api;

import java.util.Arrays;
import java.util.Map;

import org.softwareFm.crowdsource.utilities.maps.Maps;

public class ProjectFixture {
	public final static Map<String, Object> map0 = Maps.stringObjectMap(//
			"january_12", Maps.stringObjectMap(//
					"groupId0", Maps.stringObjectMap(//
							"artifact11", Arrays.asList(1, 3, 4, 5), "artifact12", Arrays.asList(1, 2, 3)),//
					"groupId1", Maps.stringObjectMap(//
							"artifact21", Arrays.asList(1, 2), "artifact22", Arrays.asList(1, 2, 3))),//
			"febuary_12", Maps.stringObjectMap(//
					"groupId0", Maps.stringObjectMap(//
							"artifact11", Arrays.asList(1, 3, 4, 5), "artifact13", Arrays.asList(1, 2, 3)),//
					"groupId1", Maps.stringObjectMap(//
							"artifact21", Arrays.asList(1, 2), "artifact22", Arrays.asList(1, 2, 3))),//
			"march_12", Maps.stringObjectMap(//
					"groupId0", Maps.stringObjectMap(//
							"artifact11", Arrays.asList(1, 3, 4, 5), "artifact12", Arrays.asList(1, 2, 3)),//
					"groupId2", Maps.stringObjectMap(//
							"artifact31", Arrays.asList(1, 2), "artifact32", Arrays.asList(1, 2, 3))));

	public final static Map<String, Object> map1 = Maps.stringObjectMap(//
			"january_12", Maps.stringObjectMap(//
					"groupId0", Maps.stringObjectMap(//
							"artifact11", Arrays.asList(1, 3), "artifact12", Arrays.asList(1, 2, 3)),//
					"groupId1", Maps.stringObjectMap(//
							"artifact21", Arrays.asList(1, 2), "artifact22", Arrays.asList(1, 2, 3))),//
			"march_12", Maps.stringObjectMap(//
					"groupId0", Maps.stringObjectMap(//
							"artifact11", Arrays.asList(1), "artifact12", Arrays.asList(1, 2, 3)),//
					"groupId2", Maps.stringObjectMap(//
							"artifact31", Arrays.asList(1, 2), "artifact32", Arrays.asList(4, 5))));

	public final static Map<String, Object> expectedMergeResultMonth1 = Maps.stringObjectMap(//
			"groupId0", Maps.stringObjectMap(//
					"artifact11", Maps.stringObjectMap("someNewSoftwareFmId0", Arrays.asList(1l, 3l, 4l, 5l), "someNewSoftwareFmId1", Arrays.asList(1l, 3l)),//
					"artifact12", Maps.stringObjectMap("someNewSoftwareFmId0", Arrays.asList(1l, 2l, 3l), "someNewSoftwareFmId1", Arrays.asList(1l, 2l, 3l))),//
			"groupId1", Maps.stringObjectMap(//
					"artifact21", Maps.stringObjectMap("someNewSoftwareFmId0", Arrays.asList(1l, 2l), "someNewSoftwareFmId1", Arrays.asList(1l, 2l)), //
					"artifact22", Maps.stringObjectMap("someNewSoftwareFmId0", Arrays.asList(1l, 2l, 3l), "someNewSoftwareFmId1", Arrays.asList(1l, 2l, 3l))));
	public final static Map<String, Object> expectedMergeResultMonth2 = Maps.stringObjectMap(//
			"groupId0", Maps.stringObjectMap(//
					"artifact11", Maps.stringObjectMap("someNewSoftwareFmId0", Arrays.asList(1l, 3l, 4l, 5l)),//
					"artifact13", Maps.stringObjectMap("someNewSoftwareFmId0", Arrays.asList(1l, 2l, 3l))),//
			"groupId1", Maps.stringObjectMap(//
					"artifact21", Maps.stringObjectMap("someNewSoftwareFmId0", Arrays.asList(1l, 2l)), //
					"artifact22", Maps.stringObjectMap("someNewSoftwareFmId0", Arrays.asList(1l, 2l, 3l))));

	public static final Map<String, Object> expectedMergeResultMonth3 = Maps.stringObjectMap(//
			"groupId0", Maps.stringObjectMap(//
					"artifact11", Maps.stringObjectMap("someNewSoftwareFmId0", Arrays.asList(1l, 3l, 4l, 5l), "someNewSoftwareFmId1", Arrays.asList(1l)),//
					"artifact12", Maps.stringObjectMap("someNewSoftwareFmId0", Arrays.asList(1l, 2l, 3l), "someNewSoftwareFmId1", Arrays.asList(1l, 2l, 3l))),//
			"groupId2", Maps.stringObjectMap(//
					"artifact31", Maps.stringObjectMap("someNewSoftwareFmId0", Arrays.asList(1l, 2l), "someNewSoftwareFmId1", Arrays.asList(1l, 2l)), //
					"artifact32", Maps.stringObjectMap("someNewSoftwareFmId0", Arrays.asList(1l, 2l, 3l), "someNewSoftwareFmId1", Arrays.asList(4l, 5l))));

	public static ProjectMock project1(String expectedSoftwareFmId, String expectedProjectCryptoKey) {
		return new ProjectMock(true).register(expectedSoftwareFmId, expectedProjectCryptoKey, map0);
	}

	public static ProjectMock project2(String expectedSoftwareFmId, String expectedProjectCryptoKey) {
		return new ProjectMock(true).register(expectedSoftwareFmId, expectedProjectCryptoKey, map1);
	}
}