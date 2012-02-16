/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.dataStore;

import java.util.Map;

/** Called for each entry in a map returned from the server as a card. If it returns a non null, then a request for more data is made */
public interface IFollowOnFragment {

	/** Called for each entry in a map returned from the server as a card. If it returns a non null, then a request for more data is made */
	String findFollowOnFragment(String key, Object value);

	public static class Utils {
		public static IFollowOnFragment followOnMaps = new IFollowOnFragment() {
			@Override
			public String findFollowOnFragment(String key, Object value) {
				if (value instanceof Map<?, ?>)
					return key;
				else
					return null;
			}
		};

	}
}