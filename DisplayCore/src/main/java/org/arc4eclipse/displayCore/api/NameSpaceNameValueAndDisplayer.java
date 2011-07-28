package org.arc4eclipse.displayCore.api;

public class NameSpaceNameValueAndDisplayer extends NameSpaceNameAndValue {

	public final IDisplayer displayer;

	public NameSpaceNameValueAndDisplayer(NameSpaceAndName nameSpaceAndName, Object value, IDisplayer displayer) {
		super(nameSpaceAndName.key, nameSpaceAndName.nameSpace, nameSpaceAndName.name, value);
		this.displayer = displayer;
	}
}
