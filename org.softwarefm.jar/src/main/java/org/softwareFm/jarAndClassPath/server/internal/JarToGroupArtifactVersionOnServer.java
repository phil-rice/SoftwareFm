/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.server.internal;

import java.util.Map;
import java.util.concurrent.Future;

import org.softwareFm.crowdsource.api.ICrowdSourceReadWriteApi;
import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.utilities.future.Futures;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.jarAndClassPath.api.IGroupArtifactVersionCallback;
import org.softwareFm.jarAndClassPath.internal.AbstractJarToGroupArtifactVersion;

public class JarToGroupArtifactVersionOnServer extends AbstractJarToGroupArtifactVersion {

	private final ICrowdSourceReadWriteApi readWriteApi;

	public JarToGroupArtifactVersionOnServer(ICrowdSourceReadWriteApi readWriteApi, IUrlGenerator jarUrlGenerator) {
		super(jarUrlGenerator);
		this.readWriteApi = readWriteApi;
	}

	@Override
	protected Future<?> processMapFrom(IFileDescription fileDescription, IGroupArtifactVersionCallback callback) {
		IGitOperations gitOperations = readWriteApi.gitOperations();
		Map<String, Object> map = gitOperations.getFile(fileDescription);
		replyTo(callback, map);
		return Futures.doneFuture(map);
	}

}