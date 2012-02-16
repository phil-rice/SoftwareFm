/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.user;

import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.common.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;

public interface IProject extends IUsageReader {

	void addProjectDetails(String softwareFmId, String groupId, String artifactId, String month, long day);

	public static class Utils {

		public static IFileDescription makeProjectFileDescription(IUrlGenerator userGenerator, Map<String, Object> userDetailMap, String month, String userCryptoKey) {
			String url = userGenerator.findUrlFor(userDetailMap);
			String projectUrl = Urls.compose(url, SoftwareFmConstants.projectDirectoryName);
			IFileDescription fileDescription = IFileDescription.Utils.encrypted(projectUrl, month, userCryptoKey);
			return fileDescription;
		}
	}

}