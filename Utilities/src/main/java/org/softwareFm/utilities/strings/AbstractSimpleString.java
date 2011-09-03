package org.softwareFm.utilities.strings;

public abstract class AbstractSimpleString implements ISimpleStringWithSetters {
	protected int start;
	protected int size;

	public int length() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setFromString(String string) {
		start = 0;
		size = string.length();
		setFromByteArray(string.getBytes(), start, size);
	}
}
