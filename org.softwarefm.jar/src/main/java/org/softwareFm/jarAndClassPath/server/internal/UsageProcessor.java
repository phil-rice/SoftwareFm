/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.server.internal;

import java.util.Map;

import org.apache.log4j.Logger;
import org.softwareFm.crowdsource.api.IContainer;
import org.softwareFm.crowdsource.api.server.AbstractCallProcessor;
import org.softwareFm.crowdsource.api.server.IProcessResult;
import org.softwareFm.crowdsource.utilities.callbacks.ICallback3;
import org.softwareFm.crowdsource.utilities.constants.CommonConstants;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;
import org.softwareFm.jarAndClassPath.api.IGroupArtifactVersionCallback;
import org.softwareFm.jarAndClassPath.api.IJarToGroupArtifactAndVersion;
import org.softwareFm.jarAndClassPath.api.IProject;
import org.softwareFm.jarAndClassPath.api.IProjectTimeGetter;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;

public class UsageProcessor extends AbstractCallProcessor {

	private static Logger logger = Logger.getLogger(IProject.class);

	private final IContainer api;

	public UsageProcessor(IContainer api) {
		super(CommonConstants.POST, JarAndPathConstants.usagePrefix);
		this.api = api;
	}

	@Override
	protected IProcessResult execute(final String actualUrl, final Map<String, Object> parameters) {
		checkForParameter(parameters, LoginConstants.softwareFmIdKey, JarAndPathConstants.digest);
		api.access(IProject.class, IProjectTimeGetter.class, IJarToGroupArtifactAndVersion.class, new ICallback3<IProject, IProjectTimeGetter, IJarToGroupArtifactAndVersion>() {
			@Override
			public void process(final IProject project, IProjectTimeGetter projectTimeGetter, IJarToGroupArtifactAndVersion jarToGroupArtifactAndVersion) throws Exception {
				final String month = projectTimeGetter.thisMonth();
				final int day = projectTimeGetter.day();
				final String digest = (String) parameters.get(JarAndPathConstants.digest);
				logger.debug("execute" + actualUrl + ", " + parameters + ", " + month + ", " + day);
				jarToGroupArtifactAndVersion.convertJarToGroupArtifactAndVersion(digest, new IGroupArtifactVersionCallback() {
					@Override
					public void process(String groupId, String artifactId, String version) {
						logger.debug("digest: " + digest + ", groupId: " + groupId + ", artifactId: " + artifactId + ", version: " + version + month + ", " + day);
						String softwareFmId = (String) parameters.get(LoginConstants.softwareFmIdKey);
						project.addProjectDetails(softwareFmId, groupId, artifactId, month, day);
					}

					@Override
					public void noData() {
					}
				});
			}
		}).get();
		return IProcessResult.Utils.doNothing();
	}

}