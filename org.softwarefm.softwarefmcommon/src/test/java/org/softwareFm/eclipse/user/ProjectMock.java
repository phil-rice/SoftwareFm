/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

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