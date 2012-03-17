/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.project;

import org.softwareFm.crowdsource.api.git.IGitLocal;
import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.url.IUrlGenerator;
import org.softwareFm.eclipse.project.internal.UsageReaderForLocal;
import org.softwareFm.eclipse.user.IUsageReader;

public class UserAndProjectFactory {

	public static IUsageReader projectForLocal(IUserReader user, IUrlGenerator userUrlGenerator, String cryptoKey, IGitLocal gitLocal) {
		return new UsageReaderForLocal(user, userUrlGenerator, gitLocal, cryptoKey);
	}
}