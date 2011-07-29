package org.arc4eclipse.displayCore.api;

public class NameSpaceNameAndValue extends NameSpaceAndName {

	public static class Utils {
		public static NameSpaceNameAndValue make(String key, Object value) {
			NameSpaceAndName ripped = NameSpaceAndName.Utils.rip(key);
			return new NameSpaceNameAndValue(key, ripped.nameSpace, ripped.name, value);
		}
	}

	public Object value;

	protected NameSpaceNameAndValue(String key, String nameSpace, String name, Object value) {
		super(key, nameSpace, name);
		this.value = value;
	}

}
