package org.arc4eclipse.displayCore.api.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.IDisplayManager;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.ITitleLookup;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
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
	public void populate(IDisplayContainer container, BindingContext bindingContext, Map<String, Object> data) {
		List<NameSpaceNameValueAndDisplayer> toBeDisplayed = Lists.newList();
		for (String key : data.keySet()) {
			Object value = data.get(key);
			NameSpaceAndName nameSpaceAndName = NameSpaceAndName.Utils.rip(key);
			IDisplayer displayer = registeredDisplayers.get(nameSpaceAndName.nameSpace);
			if (displayer == null)
				toBeDisplayed.add(new NameSpaceNameValueAndDisplayer(nameSpaceAndName, value, displayUnknown));
			else
				toBeDisplayed.add(new NameSpaceNameValueAndDisplayer(nameSpaceAndName, value, displayer));
		}
		Collections.sort(toBeDisplayed, titleLookup);
		container.addDisplayers(bindingContext, data, toBeDisplayed);
	}
}
