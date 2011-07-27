package org.arc4eclipse.displayCore.api.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.IDisplayManager;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.ITitleLookup;
import org.arc4eclipse.displayCore.api.NameSpaceNameAndValue;
import org.arc4eclipse.displayCore.api.NameSpaceNameValueAndDisplayer;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.maps.Maps;

public class DisplayManager implements IDisplayManager {

	private final Map<String, IDisplayer> registeredDisplayers = Maps.newMap();
	private final ITitleLookup titleLookup;
	private final DisplayUnknown displayUnknown = new DisplayUnknown();

	public DisplayManager(ITitleLookup titleLookup) {
		this.titleLookup = titleLookup;
	}

	@Override
	public void registerDisplayer(IDisplayer displayer) {
		registeredDisplayers.put(displayer.getNameSpace(), displayer);
	}

	@Override
	public void populate(IDisplayContainer container, Map<String, Object> jsonObject) {
		List<NameSpaceNameValueAndDisplayer> toBeDisplayed = Lists.newList();
		for (Entry<String, Object> entry : jsonObject.entrySet()) {
			NameSpaceNameAndValue nameSpaceNameAndValue = Entries.rip(entry);
			IDisplayer displayer = registeredDisplayers.get(nameSpaceNameAndValue.nameSpace);
			if (displayer == null)
				toBeDisplayed.add(new NameSpaceNameValueAndDisplayer(nameSpaceNameAndValue, displayUnknown));
			else
				toBeDisplayed.add(new NameSpaceNameValueAndDisplayer(nameSpaceNameAndValue, displayer));
		}
		container.addDisplayers(titleLookup, toBeDisplayed);
	}

}
