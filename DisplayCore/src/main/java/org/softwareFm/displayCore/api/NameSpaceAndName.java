package org.softwareFm.displayCore.api;

public class NameSpaceAndName {

	public static class Utils {
		public static NameSpaceAndName rip(String key) {
			int index = key.indexOf('_');
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

	protected NameSpaceAndName(String key, String nameSpace, String name) {
		this.key = key;
		this.nameSpace = nameSpace;
		this.name = name;
	}

	@Override
	public String toString() {
		return "NameSpaceAndName [nameSpace=" + nameSpace + ", name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nameSpace == null) ? 0 : nameSpace.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NameSpaceAndName other = (NameSpaceAndName) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nameSpace == null) {
			if (other.nameSpace != null)
				return false;
		} else if (!nameSpace.equals(other.nameSpace))
			return false;
		return true;
	}

}
