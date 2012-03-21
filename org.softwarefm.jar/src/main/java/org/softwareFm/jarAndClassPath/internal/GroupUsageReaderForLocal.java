/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.internal;

import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.api.user.IGroupsReader;
import org.softwareFm.crowdsource.utilities.constants.GroupConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.jarAndClassPath.api.IGroupUsageReader;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;

public class GroupUsageReaderForLocal implements IGroupUsageReader {

	private final IGroupsReader groupsReader;
	private final IUrlGenerator groupUrlGenerator;
	private final IGitLocal gitLocal;

	public GroupUsageReaderForLocal(IUrlGenerator groupUrlGenerator, IGitLocal gitLocal, IGroupsReader groupsReader) {
		this.groupUrlGenerator = groupUrlGenerator;
		this.gitLocal = gitLocal;
		this.groupsReader = groupsReader;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Map<String, Map<String, List<Integer>>>> getProjectDetails(String groupId, String groupCryptoKey, String month) {
		String baseUrl = findUrl(groupId);
		String projectUrl = Urls.compose(baseUrl, JarAndPathConstants.projectDirectoryName);
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(projectUrl, month, groupCryptoKey);
		return (Map) gitLocal.getFile(fileDescription);
	}

	protected String findUrl(String groupId) {
		String url = groupUrlGenerator.findUrlFor(Maps.stringObjectMap(GroupConstants.groupIdKey, groupId));
		return url;
	}

}