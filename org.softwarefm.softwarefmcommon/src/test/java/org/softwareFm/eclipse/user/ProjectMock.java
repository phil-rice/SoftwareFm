/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.softwareFm.common.maps.Maps;

public class ProjectMock implements IProject {

	/** sfmId => crypto => usage */
	private final Map<String, Map<String, Map<String, Object>>> map = Maps.newMap();
	private final boolean exceptionifNotIn;

	public ProjectMock(boolean exceptionifNotIn) {
		this.exceptionifNotIn = exceptionifNotIn;
	}

	public ProjectMock register(String expectedSoftwareFmId, String expectedProjectCryptoKey, Map<String, Object> map) {
		Maps.addToMapOfMaps(this.map, HashMap.class, expectedSoftwareFmId, expectedProjectCryptoKey, map);
		return this;
	}

	@SuppressWarnings({ "unchecked", "null" })
	@Override
	public Map<String, Map<String, List<Integer>>> getProjectDetails(String softwareFmId, String projectCryptoKey, String month) {
		Map<String, Map<String, Object>> cryptoMap = map.get(softwareFmId);
		if (cryptoMap == null)
			if (exceptionifNotIn)
				Assert.fail("Crypto map for " + softwareFmId + " not found");
			else
				return null;
		Map<String, Object> monthMap = cryptoMap.get(projectCryptoKey);
		if (monthMap == null)
			if (exceptionifNotIn)
				Assert.fail("Month  map for " + softwareFmId + ", " + projectCryptoKey + " not found");
			else
				return null;
		return (Map<String, Map<String, List<Integer>>>) monthMap.get(month);
	}

	@Override
	public void addProjectDetails(String softwareFmId, String groupId, String artifactId, String month, long day) {
		throw new UnsupportedOperationException();
	}

}