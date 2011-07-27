package org.arc4eclipse.displayCore.api;

public class NameSpaceNameValueAndDisplayer {

	public final IDisplayer displayer;
	public final NameSpaceNameAndValue nameSpaceNameAndValue;

	public NameSpaceNameValueAndDisplayer(NameSpaceNameAndValue nameSpaceNameAndValue, IDisplayer displayer) {
		this.nameSpaceNameAndValue = nameSpaceNameAndValue;
		this.displayer = displayer;

	}
}
