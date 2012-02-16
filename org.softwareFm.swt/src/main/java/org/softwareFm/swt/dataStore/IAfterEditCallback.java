/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.dataStore;

public interface IAfterEditCallback {

	/** Once data has been stored on the server, and the server has responded, this is called. The url is where the server has just stored the data */
	void afterEdit(String url);

	public static class Utils {
		public static MemoryAfterEditCallback memory() {
			return new MemoryAfterEditCallback();
		}
	}
}