/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.internal;

import java.io.File;
import java.util.Map;

import org.softwareFm.common.IFileDescription;
import org.softwareFm.common.IGitOperations;
import org.softwareFm.common.IGitWriter;

public class GitWriterForTests implements IGitWriter {
	private final IGitOperations remoteOperations;
	

	public GitWriterForTests(IGitOperations remoteOperations) {
		super();
		this.remoteOperations = remoteOperations;
	}

	@Override
	public void put(IFileDescription fileDescription, Map<String, Object> data) {
		remoteOperations.put(fileDescription, data);
	}

	@Override
	public void init(String url) {
		remoteOperations.init(url);
	}

	@Override
	public void delete(IFileDescription fileDescription) {
		File file = new File(remoteOperations.getRoot(), fileDescription.url());
		file.delete();
		IFileDescription.Utils.addAllAndCommit(remoteOperations, fileDescription, "delete: " + fileDescription);
	}
}