/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.user;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.constants.LoginConstants;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;

abstract public class AbstractUsageReader implements IUsageReader {

	protected final IUserReader user;
	protected final IUrlGenerator userUrlGenerator;

	public AbstractUsageReader(IUserReader user, IUrlGenerator userUrlGenerator) {
		this.user = user;
		this.userUrlGenerator = userUrlGenerator;
	}

	@Override
	public Map<String, Map<String, List<Integer>>> getProjectDetails(String softwareFmId, String projectCryptoKey, String month) {
		String userUrl = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId));
		IFileDescription projectFileDescription = IFileDescription.Utils.encrypted(Urls.compose(userUrl, SoftwareFmConstants.projectDirectoryName), month, projectCryptoKey);

		Map<String, Map<String, List<Integer>>> projectDetails = getProjectDetails(projectFileDescription);
		return projectDetails;
	}

	abstract protected Map<String, Map<String, List<Integer>>> getProjectDetails(IFileDescription projectFileDescription);



}