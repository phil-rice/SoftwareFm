/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.softwareFmServer;

import java.util.List;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IUserReader;
import org.softwareFm.common.url.IUrlGenerator;
import org.softwareFm.eclipse.user.AbstractUsageReader;

public class UsageReaderForServer extends AbstractUsageReader {

	protected final IGitOperations gitOperations;

	public UsageReaderForServer(IGitOperations gitOperations, IUserReader user, IUrlGenerator userUrlGenerator) {
		super(user, userUrlGenerator);
		this.gitOperations = gitOperations;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Map<String, Map<String, List<Integer>>> getProjectDetails(IFileDescription projectFileDescription) {
		Map<String, Map<String, List<Integer>>> projectDetails = (Map) gitOperations.getFile(projectFileDescription);
		return projectDetails;
	}

}