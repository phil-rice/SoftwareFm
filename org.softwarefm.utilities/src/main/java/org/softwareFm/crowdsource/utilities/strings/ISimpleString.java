/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.strings;

import org.softwareFm.crowdsource.utilities.annotations.Slow;

/** This is a very simple string that uses UTF-8 character encoding. It is small and poolable */
public interface ISimpleString {

	int length();

	byte byteAt(int offset);

	@Slow("This will make a new object")
	String asString();

	abstract static class Utils {

		public static boolean equivalent(ISimpleString simpleString, String string) {
			if (simpleString == null || string == null)
				return simpleString == null && string == null;
			int length = simpleString.length();
			if (length != string.length())
				return false;
			for (int i = 0; i < length; i++)
				if (simpleString.byteAt(i) != string.charAt(i))
					return false;
			return true;
		}

		public static final ISimpleString empty = new ISimpleString() {
			@Override
			public int length() {
				return 0;
			}

			@Override
			public byte byteAt(int offset) {
				throw new IndexOutOfBoundsException(Integer.toString(offset));
			}

			@Override
			public String asString() {
				return "";
			}
		};

		public static boolean equivalent(String string, ISimpleString simpleString) {
			if (string == null || simpleString == null)
				return string == null && simpleString == null;
			if (string.length() != simpleString.length())
				return false;
			for (int i = 0; i < string.length(); i++)
				if ((byte) string.charAt(i) != simpleString.byteAt(i))
					return false;
			return true;
		}

	}

}