/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.server.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.softwareFm.crowdsource.api.ICrowdSourcedReadWriteApi;
import org.softwareFm.crowdsource.api.IUserCryptoAccess;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.crowdsource.utilities.url.Urls;
import org.softwareFm.jarAndClassPath.api.IProject;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;

public class ProjectForServer implements IProject {
	private static Logger logger = Logger.getLogger(IProject.class);
	private final IUrlGenerator userUrlGenerator;
	private final IUserCryptoAccess userCryptoAccess;
	private final ICrowdSourcedReadWriteApi readWriteApi;

	public ProjectForServer(ICrowdSourcedReadWriteApi readWriteApi, IUserCryptoAccess userCryptoAccess, IUrlGenerator userUrlGenerator) {
		this.readWriteApi = readWriteApi;
		this.userCryptoAccess = userCryptoAccess;
		this.userUrlGenerator = userUrlGenerator;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Map<String, List<Integer>>> getProjectDetails(String softwareFm, String projectCryptoKey, String month) {
		IGitOperations gitOperations = readWriteApi.gitOperations();
		final IFileDescription projectFileDescription = getFileDescriptionForProject(softwareFm, month);
		Map<String, Map<String, List<Integer>>> projectDetails = (Map) gitOperations.getFile(projectFileDescription);
		return projectDetails;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addProjectDetails(String softwareFmId, String groupId, String artifactId, String month, long day) {
		IFileDescription projectFileDescription = getFileDescriptionForProject(softwareFmId, month);
		IGitOperations gitOperations = readWriteApi.gitOperations();
		logger.debug(projectFileDescription);
		Map<String, Object> initialProjectDetails = gitOperations.getFile(projectFileDescription);
		logger.debug("Initial Project Details: " + initialProjectDetails);
		Map<String, Object> groupDetails = (Map<String, Object>) Maps.getOrDefault(initialProjectDetails, groupId, Maps.emptyStringObjectMap());
		List<Long> artifactDetails = (List<Long>) Maps.getOrDefault(groupDetails, artifactId, Collections.<Integer> emptyList());
		if (!artifactDetails.contains(day)) {
			logger.debug("Adding to  Project Details");
			List<Long> newDays = Lists.append(artifactDetails, day);
			Map<String, Object> newGroupData = Maps.with(groupDetails, artifactId, newDays);
			Map<String, Object> newProjectData = Maps.with(initialProjectDetails, groupId, newGroupData);
			gitOperations.put(projectFileDescription, newProjectData);
			logger.debug("New  Project Details: " + newProjectData);
		}
	}

	protected IFileDescription getFileDescriptionForProject(String softwareFmId, String month) {
		Map<String, Object> userDetailMap = Maps.stringObjectMap(LoginConstants.softwareFmIdKey, softwareFmId);
		String userUrl = userUrlGenerator.findUrlFor(userDetailMap);
		logger.debug("User url: " + userUrl);
		String cryptoKey = userCryptoAccess.getCryptoForUser((String) userDetailMap.get(LoginConstants.softwareFmIdKey));
		logger.debug("User crypto key: " + cryptoKey);
		String projectCryptoKey = IUserReader.Utils.getUserProperty(readWriteApi, softwareFmId, cryptoKey, JarAndPathConstants.projectCryptoKey);
		if (projectCryptoKey == null)
			throw new NullPointerException();
		logger.debug("Project crypto key: " + cryptoKey);
		IFileDescription projectFileDescription = IFileDescription.Utils.encrypted(Urls.compose(userUrl, JarAndPathConstants.projectDirectoryName), month, projectCryptoKey);
		return projectFileDescription;
	}

}