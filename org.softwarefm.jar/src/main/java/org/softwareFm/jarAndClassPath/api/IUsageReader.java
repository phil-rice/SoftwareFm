/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.jarAndClassPath.api;

import java.util.List;
import java.util.Map;

import org.softwareFm.crowdsource.api.ICrowdSourcedReaderApi;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.jarAndClassPath.internal.UsageReaderForLocal;

public interface IUsageReader {
	/** group -> artifact -> list of the days */
	Map<String, Map<String, List<Integer>>> getProjectDetails(String softwareFmId, String usersProjectCryptoKey, String month);

	public static class Utils {
		public static IUsageReader localUsageReader(ICrowdSourcedReaderApi readerApi, IUrlGenerator userUrlGenerator) {
			return new UsageReaderForLocal(readerApi, userUrlGenerator);
		}
	}

}