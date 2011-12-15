/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.server.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.softwareFm.repositoryFacard.IRepositoryFacardCallback;
import org.softwareFm.repositoryFacard.IRepositoryFacardReader;
import org.softwareFm.server.GetResult;
import org.softwareFm.server.ILocalGit;
import org.softwareFm.server.ILocalGitReader;
import org.softwareFm.utilities.services.IServiceExecutor;

/** This class reads from the local file system. If the file isn't held locally, it asks for a git download, then returns the file. */
public class GitRepositoryFacard implements IRepositoryFacardReader {

	private final IServiceExecutor serviceExecutor;
	private final ILocalGitReader localGit;

	public GitRepositoryFacard(IServiceExecutor serviceExecutor, ILocalGitReader localGit) {
		this.serviceExecutor = serviceExecutor;
		this.localGit = localGit;
	}

	@Override
	public Future<?> get(final String url, final IRepositoryFacardCallback callback) {
		return serviceExecutor.submit(new Callable<GetResult>() {
			@Override
			public GetResult call() throws Exception {
				return ILocalGit.Utils.getFromLocalPullIfNeeded(localGit, url);
			}
		});
	}

}