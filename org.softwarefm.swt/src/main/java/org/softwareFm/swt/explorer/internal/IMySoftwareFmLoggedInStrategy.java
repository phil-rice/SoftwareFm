/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.explorer.internal;

import java.util.Arrays;
import java.util.List;

import org.softwareFm.crowdsource.api.user.IUserReader;
import org.softwareFm.crowdsource.utilities.constants.LoginConstants;

public interface IMySoftwareFmLoggedInStrategy {
	IUserReader userReader();

	void logout();

	void showMyData();

	void showMyGroups();


	List<String> displayProperties();

	public static class Utils {
		public static IMySoftwareFmLoggedInStrategy sysout(final Object... nameAndValues) {
			return new IMySoftwareFmLoggedInStrategy() {
				@Override
				public IUserReader userReader() {
					return IUserReader.Utils.mockReader(nameAndValues);
				}

				@Override
				public void showMyGroups() {
					System.out.println("Show My Groups");
				}

				@Override
				public void showMyData() {
					System.out.println("Show My Data");
				}

				@Override
				public void logout() {
					System.out.println("Logout");
				}

				@Override
				public List<String> displayProperties() {
					return Arrays.asList(LoginConstants.emailKey, LoginConstants.monikerKey);
				}
			};
		}
	}
}