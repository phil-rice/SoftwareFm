/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.explorer;

import org.softwareFm.jarAndClassPath.api.UserData;

public interface IShowMyPeople {
	void showMyPeople(UserData userData, String groupId, String artifactId);

	public static class Utils {
		public static IShowMyPeople sysoutShowMyPeople() {
			return new IShowMyPeople() {
				@Override
				public void showMyPeople(UserData userData, String groupId, String artifactId) {
					System.out.println("ShowMyPeople: " + userData + ", " + groupId + ", " + artifactId);
				}
			};
		}

		public static IShowMyPeople exceptionShowMyPeople() {
			return new IShowMyPeople() {
				@Override
				public void showMyPeople(UserData userData, String groupId, String artifactId) {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

}