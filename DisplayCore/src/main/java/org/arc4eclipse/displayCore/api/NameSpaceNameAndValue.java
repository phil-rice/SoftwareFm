package org.arc4eclipse.displayCore.api;

public class NameSpaceNameAndValue extends NameSpaceAndName {

	public Object value;

	public NameSpaceNameAndValue(String key, String nameSpace, String name, Object value) {
		super(key, nameSpace, name);
		this.value = value;
	}

}
