/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.eclipse.jdtBinding;

import java.text.MessageFormat;

public interface IJavadocSourceMutatorCallback {

	void process(String requested, String actual);

	public static class Utils {
		public static IJavadocSourceMutatorCallback sysout(final String pattern) {
			return new IJavadocSourceMutatorCallback() {
				@Override
				public void process(String requested, String actual) {
					System.out.println(MessageFormat.format(pattern, requested, actual));
				}
			};
		}

		public static IJavadocSourceMutatorCallback sysout() {
			return sysout("Mutated {0},{1}");
		}

		public static IJavadocSourceMutatorCallback blank() {
			return new IJavadocSourceMutatorCallback() {
				@Override
				public void process(String requested, String actual) {
				}
			};
		}
	}
}