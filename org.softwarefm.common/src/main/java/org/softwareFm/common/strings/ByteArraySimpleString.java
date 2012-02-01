/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.strings;

public class ByteArraySimpleString extends AbstractSimpleString {

	private final byte[] byteArray;
	private int start;
	private int size;

	public ByteArraySimpleString(byte[] byteArray) {
		this.byteArray = byteArray;
	}

	@Override
	public byte byteAt(int offset) {
		return byteArray[start + offset];
	}

	@Override
	public void setFromByteArray(byte[] byteArray, int start, int size) {
		for (int i = 0; i < size; i++)
			this.byteArray[i] = byteArray[start + i];
		this.start = 0;
		this.size = size;
	}

	@Override
	public String asString() {
		return new String(byteArray, start, size);
	}

}