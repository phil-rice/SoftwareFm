/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.user;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitLocal;
import org.softwareFm.common.LocalGroupsReader;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;

public class GroupUsageReaderForLocal extends LocalGroupsReader implements IGroupUsageReader {

	public GroupUsageReaderForLocal(IUrlGenerator urlGenerator, IGitLocal gitLocal) {
		super(urlGenerator, gitLocal);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Map<String, Map<String, List<Integer>>>> getProjectDetails(String groupId, String groupCryptoKey, String month) {
		String baseUrl = findUrl(groupId);
		String projectUrl = Urls.compose(baseUrl, SoftwareFmConstants.projectDirectoryName);
		IFileDescription fileDescription = IFileDescription.Utils.encrypted(projectUrl, month, groupCryptoKey);
		return (Map) gitLocal.getFile(fileDescription);
	}

}