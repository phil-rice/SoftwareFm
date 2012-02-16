/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.callbacks;

public interface ICallback2<T1, T2> {
	void process(T1 first, T2 second) throws Exception;

	abstract static class Utils {
		public static final <T1, T2> ICallback2<T1, T2> noCallback() {
			return new NoCallback2<T1, T2>();
		}

	}
}