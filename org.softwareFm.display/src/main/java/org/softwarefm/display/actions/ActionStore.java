package org.softwarefm.display.actions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.softwareFm.utilities.maps.ISimpleMap;
import org.softwareFm.utilities.maps.Maps;
import org.softwarefm.display.IAction;
import org.softwarefm.display.data.DisplayConstants;

public class ActionStore implements ISimpleMap<String, IAction> {

	private final Map<String, IAction> map = Maps.newMap(LinkedHashMap.class);

	public ActionStore action(String string, IAction action) {
		if (map.containsKey(string))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "action", map.get(string), action));
		map.put(string, action);
		return this;
	}

	@Override
	public IAction get(String key) {
		return Maps.getOrException(map, key);
	}

	@Override
	public List<String> keys() {
		return new ArrayList<String>(map.keySet());
	}

}
