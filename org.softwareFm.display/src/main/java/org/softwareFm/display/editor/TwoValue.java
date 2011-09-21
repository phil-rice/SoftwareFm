package org.softwareFm.display.editor;

public class TwoValue {

	public String value1;
	public String value2;


	public TwoValue(String value1, String value2) {
		this.value1 = value1;
		this.value2 = value2;
	}


	@Override
	public String toString() {
		return "TwoValue [value1=" + value1 + ", value2=" + value2 + "]";
	}
}
