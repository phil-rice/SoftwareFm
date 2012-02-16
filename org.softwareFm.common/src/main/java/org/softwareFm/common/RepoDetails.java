/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common;

import java.util.Map;

public class RepoDetails {

	public static RepoDetails repositoryUrl(String repositoryUrl) {
		if (repositoryUrl == null)
			throw new IllegalArgumentException();
		return new RepoDetails(null, repositoryUrl);
	}

	public static RepoDetails aboveRepo(Map<String, Object> map) {
		if (map == null)
			throw new IllegalArgumentException();
		return new RepoDetails(map, null);
	}

	private final Map<String, Object> data;
	private final String repositoryUrl;

	private RepoDetails(Map<String, Object> data, String repositoryUrl) {
		this.data = data;
		this.repositoryUrl = repositoryUrl;
		assert data == null || repositoryUrl == null;
		assert !(data == null && repositoryUrl == null);
	}

	public boolean aboveRepository() {
		return data != null;
	}

	public Map<String, Object> getAboveRepositoryData() {
		if (data == null)
			throw new IllegalStateException();
		return data;
	}

	public String getRepositoryUrl() {
		if (repositoryUrl == null)
			throw new IllegalStateException();
		return repositoryUrl;
	}

}