package org.arc4eclipse.displayCore.api.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.displayCore.api.BindingContext;
import org.arc4eclipse.displayCore.api.IDisplayContainer;
import org.arc4eclipse.displayCore.api.IDisplayManager;
import org.arc4eclipse.displayCore.api.IDisplayer;
import org.arc4eclipse.displayCore.api.IModifiesToBeDisplayed;
import org.arc4eclipse.displayCore.api.ITitleLookup;
import org.arc4eclipse.displayCore.api.NameSpaceAndName;
import org.arc4eclipse.displayCore.api.NameSpaceNameAndValue;
import org.arc4eclipse.displayCore.api.NameSpaceNameValueAndDisplayer;
import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;
import org.arc4eclipse.utilities.collections.Lists;
import org.arc4eclipse.utilities.maps.Maps;

public class DisplayManager implements IDisplayManager {

	private final Map<String, IDisplayer> registeredDisplayers = Maps.newMap();
	private final ITitleLookup titleLookup;
	private final DisplayUnknown displayUnknown = new DisplayUnknown();
	private final List<IModifiesToBeDisplayed> modifiers = Lists.newList();

	public DisplayManager(ITitleLookup titleLookup) {
		this.titleLookup = titleLookup;
	}

	@Override
	public void registerDisplayer(IDisplayer displayer) {
		registeredDisplayers.put(displayer.getNameSpace(), displayer);
	}

	@Override
	public void populate(IDisplayContainer container, BindingContext bindingContext, String url, Map<String, Object> data, Map<String, Object> context) {
		List<NameSpaceNameValueAndDisplayer> toBeDisplayed = Lists.newList();
		if (data != null)
			for (String key : data.keySet()) {
				if (DisplayCoreConstants.keysToIgnore.contains(key))
					continue;
				Object value = data.get(key);
				NameSpaceAndName nameSpaceAndName = NameSpaceAndName.Utils.rip(key);
				addFor(toBeDisplayed, nameSpaceAndName, value);
			}
		modifyData(toBeDisplayed, data, context);
		Collections.sort(toBeDisplayed, titleLookup);
		container.addDisplayers(bindingContext, url, data, toBeDisplayed);
	}

	private void addFor(List<NameSpaceNameValueAndDisplayer> toBeDisplayed, NameSpaceAndName nameSpaceAndName, Object value) {
		IDisplayer displayer = registeredDisplayers.get(nameSpaceAndName.nameSpace);
		if (displayer == null)
			toBeDisplayed.add(NameSpaceNameValueAndDisplayer.display(nameSpaceAndName.key, value, displayUnknown));
		else
			toBeDisplayed.add(NameSpaceNameValueAndDisplayer.display(nameSpaceAndName.key, value, displayer));
	}

	private void modifyData(List<NameSpaceNameValueAndDisplayer> toBeDisplayed, Map<String, Object> data, Map<String, Object> context) {
		for (IModifiesToBeDisplayed modifier : modifiers) {
			List<NameSpaceNameAndValue> added = modifier.add(data, context);
			for (NameSpaceNameAndValue nameSpaceNameAndValue : added)
				addFor(toBeDisplayed, nameSpaceNameAndValue, nameSpaceNameAndValue.value);
		}
	}

	@Override
	public void addModifier(IModifiesToBeDisplayed modifier) {
		modifiers.add(modifier);
	}

	@Override
	public void removeModifier(IModifiesToBeDisplayed modifier) {
		modifiers.remove(modifier);
	}

}
