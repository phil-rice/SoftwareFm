/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.maps.Maps;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.constants.SoftwareFmConstants;

public abstract class AbstractJarToGroupArtifactVersion implements IJarToGroupArtifactAndVersion {

	private final IUrlGenerator jarUrlGenerator;

	public AbstractJarToGroupArtifactVersion(IUrlGenerator jarUrlGenerator) {
		this.jarUrlGenerator = jarUrlGenerator;
	}

	@Override
	public Future<?> convertJarToGroupArtifactAndVersion(String jarDigest, IGroupArtifactVersionCallback callback) {
		String url = jarUrlGenerator.findUrlFor(Maps.stringObjectMap(SoftwareFmConstants.digest, jarDigest));
		IFileDescription fileDescription = IFileDescription.Utils.plain(url);
		return processMapFrom(fileDescription, callback);
	}

	abstract protected Future<?> processMapFrom(IFileDescription fileDescription, IGroupArtifactVersionCallback callback);

	protected void replyTo(IGroupArtifactVersionCallback callback, Map<String, Object> map) {
		if (map == null)
			callback.noData();
		else {
			String groupId = (String) map.get(SoftwareFmConstants.groupId);
			String artifactId = (String) map.get(SoftwareFmConstants.artifactId);
			String version = (String) map.get(SoftwareFmConstants.version);
			callback.process(groupId, artifactId, version);
		}
	}

}