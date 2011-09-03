package org.softwareFm.utilities.strings;

public class ByteArraySimpleString extends AbstractSimpleString {

	private final byte[] byteArray;
	private int start;
	private int size;

	public ByteArraySimpleString(byte[] byteArray) {
		this.byteArray = byteArray;
	}

	public byte byteAt(int offset) {
		return byteArray[start + offset];
	}

	public void setFromByteArray(byte[] byteArray, int start, int size) {
		for (int i = 0; i < size; i++)
			this.byteArray[i] = byteArray[start + i];
		this.start = 0;
		this.size = size;
	}

	public String asString() {
		return new String(byteArray, start, size);
	}

}
