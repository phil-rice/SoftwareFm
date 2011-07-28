package org.arc4eclipse.displayCore.api.impl;

import org.arc4eclipse.displayCore.api.ITitleLookup;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;

public class TitleLookup implements ITitleLookup {

	@Override
	public String getTitle(String nameSpace, String name) {
		return "<" + name + ">";
	}

	@Override
	public int compare(NameSpaceAndName o1, NameSpaceAndName o2) {
		String ns1 = o1.nameSpace;
		String ns2 = o2.nameSpace;
		int c1 = ns1.compareTo(ns2);
		if (c1 != 0)
			return c1;
		String n1 = o1.name;
		String n2 = o2.name;
		int c2 = n1.compareTo(n2);
		return c2;
	}
}
