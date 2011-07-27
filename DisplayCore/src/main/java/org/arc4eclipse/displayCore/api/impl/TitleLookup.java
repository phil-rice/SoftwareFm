package org.arc4eclipse.displayCore.api.impl;

import org.arc4eclipse.displayCore.api.ITitleLookup;

public class TitleLookup implements ITitleLookup {

	@Override
	public String getTitle(String nameSpace, String name) {
		return "<" + name + ">";
	}

}
