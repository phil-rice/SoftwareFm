package org.softwareFm.utilities.strings;

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
