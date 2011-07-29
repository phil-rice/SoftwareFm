package org.arc4eclipse.displayCore.api;

public class NameSpaceNameValueAndDisplayer extends NameSpaceNameAndValue {

	public static NameSpaceNameValueAndDisplayer display(String key, Object value, IDisplayer displayer) {
		return new NameSpaceNameValueAndDisplayer(NameSpaceAndName.Utils.rip(key), value, displayer);
	}

	public final IDisplayer displayer;

	protected NameSpaceNameValueAndDisplayer(NameSpaceAndName nameSpaceAndName, Object value, IDisplayer displayer) {
		super(nameSpaceAndName.key, nameSpaceAndName.nameSpace, nameSpaceAndName.name, value);
		this.displayer = displayer;
	}
}
