package org.arc4eclipse.displayCore.api.impl;

import java.util.Map.Entry;

import org.arc4eclipse.displayCore.api.NameSpaceNameAndValue;

public class Entries {

	public static NameSpaceNameAndValue rip(Entry<String, Object> entry) {
		String nameSpaceAndName = entry.getKey();
		int index = nameSpaceAndName.indexOf(':');
		if (index == -1)
			return new NameSpaceNameAndValue(nameSpaceAndName, nameSpaceAndName, entry.getValue());
		else {
			String nameSpace = nameSpaceAndName.substring(0, index);
			String name = nameSpaceAndName.substring(index + 1);
			return new NameSpaceNameAndValue(nameSpace, name, entry.getValue());
		}

	}

}
