/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.processors.internal;

import java.io.File;
import java.util.Map;

import org.apache.http.RequestLine;
import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.collections.Files;
import org.softwareFm.common.constants.CommonConstants;
import org.softwareFm.server.processors.IProcessCall;
import org.softwareFm.server.processors.IProcessResult;

public class DeleteProcessor implements IProcessCall {

	private final IGitOperations gitOperations;

	public DeleteProcessor(IGitOperations gitOperations) {
		this.gitOperations = gitOperations;
	}

	@Override
	public IProcessResult process(RequestLine requestLine, Map<String, Object> parameters) {
		if (requestLine.getMethod().equals(CommonConstants.DELETE)) {
			String uri = requestLine.getUri();
			IFileDescription fileDescription = IFileDescription.Utils.fromRequest(requestLine, parameters);
			File root = gitOperations.getRoot();
			File file = fileDescription.getFile(root);
			if (file.exists()) {
				file.delete();
				File repositoryFile = fileDescription.findRepositoryUrl(root);
				String repositoryUrl = Files.offset(root, repositoryFile);
				gitOperations.addAllAndCommit(repositoryUrl, "delete: " + uri);
			}
			return IProcessResult.Utils.doNothing();
		}
		return null;
	}

}