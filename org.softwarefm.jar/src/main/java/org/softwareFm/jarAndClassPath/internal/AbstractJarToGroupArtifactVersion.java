/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.internal;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.utilities.maps.Maps;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.jarAndClassPath.api.IGroupArtifactVersionCallback;
import org.softwareFm.jarAndClassPath.api.IJarToGroupArtifactAndVersion;
import org.softwareFm.jarAndClassPath.constants.JarAndPathConstants;

public abstract class AbstractJarToGroupArtifactVersion implements IJarToGroupArtifactAndVersion {

	private final IUrlGenerator jarUrlGenerator;

	public AbstractJarToGroupArtifactVersion(IUrlGenerator jarUrlGenerator) {
		this.jarUrlGenerator = jarUrlGenerator;
	}

	@Override
	public Future<?> convertJarToGroupArtifactAndVersion(String jarDigest, IGroupArtifactVersionCallback callback) {
		String url = jarUrlGenerator.findUrlFor(Maps.stringObjectMap(JarAndPathConstants.digest, jarDigest));
		IFileDescription fileDescription = IFileDescription.Utils.plain(url);
		return processMapFrom(fileDescription, callback);
	}

	abstract protected Future<?> processMapFrom(IFileDescription fileDescription, IGroupArtifactVersionCallback callback);

	protected void replyTo(IGroupArtifactVersionCallback callback, Map<String, Object> map) {
		if (map == null)
			callback.noData();
		else {
			String groupId = (String) map.get(JarAndPathConstants.groupId);
			String artifactId = (String) map.get(JarAndPathConstants.artifactId);
			String version = (String) map.get(JarAndPathConstants.version);
			callback.process(groupId, artifactId, version);
		}
	}

}