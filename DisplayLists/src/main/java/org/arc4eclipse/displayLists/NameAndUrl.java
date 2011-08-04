package org.arc4eclipse.displayLists;

public class NameAndUrl {

	public String name;
	public String url;

	public NameAndUrl(String name, String url) {
		this.name = name;
		this.url = url;
	}

	@Override
	public String toString() {
		return "NameAndUrl [name=" + name + ", url=" + url + "]";
	}

}
