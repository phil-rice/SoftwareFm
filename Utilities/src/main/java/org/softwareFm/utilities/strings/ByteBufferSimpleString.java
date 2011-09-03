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
