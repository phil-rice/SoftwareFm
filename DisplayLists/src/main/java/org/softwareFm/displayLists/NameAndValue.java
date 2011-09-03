package org.softwareFm.displayLists;

public class NameAndValue {

	public String name;
	public String url;

	public NameAndValue(String name, String url) {
		this.name = name;
		this.url = url;
	}

	@Override
	public String toString() {
		return "NameAndValue [name=" + name + ", url=" + url + "]";
	}

}
