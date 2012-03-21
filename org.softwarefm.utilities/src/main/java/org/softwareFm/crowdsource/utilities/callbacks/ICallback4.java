/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.callbacks;

public interface ICallback4<T1, T2, T3,T4> {
	void process(T1 first, T2 second, T3 third, T4 fourth) throws Exception;

	abstract static class Utils {
		public static final <T1, T2, T3, T4> ICallback4<T1, T2, T3, T4> noCallback() {
			return new ICallback4<T1, T2, T3, T4>() {
				@Override
				public void process(T1 first, T2 second, T3 third, T4 fourth) throws Exception {
				}
			};
		}

	}
}