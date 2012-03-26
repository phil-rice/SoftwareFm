/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.user.internal;

import org.softwareFm.crowdsource.api.git.IFileDescription;
import org.softwareFm.crowdsource.api.git.IGitOperations;
import org.softwareFm.crowdsource.api.git.IRepoFinder;
import org.softwareFm.crowdsource.api.git.RepoDetails;

public class RepoFinderForTests implements IRepoFinder {
	private final IGitOperations remoteOperations;

	public RepoFinderForTests(IGitOperations remoteOperations) {
		this.remoteOperations = remoteOperations;
	}

	@Override
	public RepoDetails findRepoUrl(String url) {
		String repositoryUrl = IFileDescription.Utils.findRepositoryUrl(remoteOperations.getRoot(), url);
		if (repositoryUrl == null)
			return RepoDetails.aboveRepo(remoteOperations.getFileAndDescendants(IFileDescription.Utils.plain(url)));
		else
			return RepoDetails.repositoryUrl(repositoryUrl);
	}

	@Override
	public void clearCaches() {
	}

	@Override
	public void clearCache(String url) {

	}
}