package org.arc4eclipse.displayCore.api;

public class NameSpaceAndName {

	public static class Utils {
		public static NameSpaceAndName rip(String key) {
			int index = key.indexOf(':');
			if (index == -1)
				return new NameSpaceAndName(key, key, key);
			else {
				String nameSpace = key.substring(0, index);
				String name = key.substring(index + 1);
				return new NameSpaceAndName(key, nameSpace, name);
			}

		}
	}

	public String key;
	public String nameSpace;
	public String name;

	public NameSpaceAndName(String key, String nameSpace, String name) {
		this.key = key;
		this.nameSpace = nameSpace;
		this.name = name;
	}

}
