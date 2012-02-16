/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.softwareFmServer;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.future.Futures;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.AbstractJarToGroupArtifactVersion;
import org.softwareFm.eclipse.IGroupArtifactVersionCallback;

public class JarToGroupArtifactVersionOnServer extends AbstractJarToGroupArtifactVersion {


	private final IGitOperations gitOperations;

	public JarToGroupArtifactVersionOnServer(IUrlGenerator jarUrlGenerator, IGitOperations gitOperations) {
		super(jarUrlGenerator);
		this.gitOperations = gitOperations;
	}

	@Override
	protected Future<?> processMapFrom(IFileDescription fileDescription, IGroupArtifactVersionCallback callback) {
		Map<String, Object> map = gitOperations.getFile(fileDescription);
		replyTo(callback, map);
		return Futures.doneFuture(map);
	}

}