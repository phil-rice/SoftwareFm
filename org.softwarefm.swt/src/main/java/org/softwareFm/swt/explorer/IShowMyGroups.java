/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.explorer;

import org.softwareFm.crowdsource.api.UserData;

public interface IShowMyGroups {

	/** As well as showing, tries to select and scroll to make visible the groupId if it exists. GroupId can be null */
	void show(UserData data, String groupId);

	public static class Utils {

		public static IShowMyGroups sysoutShowMyGroups() {
			return new IShowMyGroups() {
				@Override
				public void show(UserData data, String groupId) {
					System.out.println("My groups: " + groupId + "/" + data);
				}
			};
		}

		public static IShowMyGroups exceptionShowMyGroups() {
			return new IShowMyGroups() {
				@Override
				public void show(UserData data, String groupId) {
					throw new UnsupportedOperationException();
				}
			};
		}

	}
}