/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.strings;

import java.nio.ByteBuffer;

public class ByteBufferSimpleString extends AbstractSimpleString {

	private final ByteBuffer byteBuffer;

	public ByteBufferSimpleString(ByteBuffer buffer) {
		this.byteBuffer = buffer;
	}

	public byte byteAt(int offset) {
		return byteBuffer.get(start + offset);
	}

	public int length() {
		return size;
	}

	public void setFromByteArray(byte[] byteArray, int start, int size) {
		for (int i = 0; i < start; i++)
			byteBuffer.put(byteArray, start, size);
	}

	public String asString() {
		return new String(byteBuffer.array(), start, size);
	}

}