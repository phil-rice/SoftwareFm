/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.strings;

public class NameAndValue {
	public static NameAndValue fromString(String withColonSeparator) {
		int index = withColonSeparator.indexOf(':');
		if (index == -1)
			return new NameAndValue(withColonSeparator, null);
		else
			return new NameAndValue(withColonSeparator.substring(0, index), withColonSeparator.substring(index + 1));

	}

	public String name;
	public String value;

	public NameAndValue(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String toString() {
		return "NameAndValue [name=" + name + ", url=" + value + "]";
	}

}