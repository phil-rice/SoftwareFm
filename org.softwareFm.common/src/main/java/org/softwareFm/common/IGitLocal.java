/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common;

import java.util.Map;

import org.softwareFm.common.internal.GitLocal;
import org.softwareFm.common.maps.IHasUrlCache;

/** These are all blocking calls that may take a long time to execute */
public interface IGitLocal extends IGitReader, IHasUrlCache {
	void init(String url);

	void put(IFileDescription fileDescription, Map<String, Object> data);

	void delete(IFileDescription fileDescription);

	abstract public static class Utils {

		public static IGitLocal localReader(IRepoFinder repoFinder, IGitOperations gitOperations, IGitWriter gitWriter, String remotePrefix, int period) {
			return new GitLocal(repoFinder, gitOperations, gitWriter, remotePrefix, period);
		}

	}

}