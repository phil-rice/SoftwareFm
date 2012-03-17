/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.project.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;
import org.softwareFm.eclipse.user.AbstractUsageReader;

public class UsageReaderForLocal extends AbstractUsageReader {

	private final IGitLocal gitLocal;
	private final String userCryptoKey;

	public UsageReaderForLocal(IUserReader user, IUrlGenerator userUrlGenerator, IGitLocal gitLocal, String userCryptoKey) {
		super(user, userUrlGenerator);
		this.gitLocal = gitLocal;
		this.userCryptoKey = userCryptoKey;
	}


	@Override
	public Map<String, Map<String, List<Integer>>> getProjectDetails(String softwareFm, String projectCryptoKey, String month) {
		IFileDescription projectFileDescription = getFileDescriptionForProject(userCryptoKey, softwareFm, month);
		return getProjectDetails(projectFileDescription);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Map<String, Map<String, List<Integer>>> getProjectDetails(IFileDescription projectFileDescription) {
		if (projectFileDescription != null) {
			Map<String, Map<String, List<Integer>>> data = (Map) gitLocal.getFile(projectFileDescription);
			if (data != null)
				return data;
		}
		return Collections.emptyMap();
	}

	protected IFileDescription getFileDescriptionForProject(String userCryptoKey, String userId, String month) {
		String projectCryptoKey = user.getUserProperty(userId, userCryptoKey, SoftwareFmConstants.projectCryptoKey);
		if (projectCryptoKey == null)
			return null;// there is nothing to display for this user
		String userUrl = userUrlGenerator.findUrlFor(Maps.stringObjectMap(LoginConstants.softwareFmIdKey, userId));
		IFileDescription projectFileDescription = IFileDescription.Utils.encrypted(Urls.compose(userUrl, SoftwareFmConstants.projectDirectoryName), month, projectCryptoKey);
		return projectFileDescription;
	}

}