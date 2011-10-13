package org.softwareFm.display.actions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.utilities.maps.ISimpleMap;
import org.softwareFm.utilities.maps.Maps;

public class ActionStore implements ISimpleMap<String, IAction> {

	private final Map<String, IAction> map = Maps.newMap(LinkedHashMap.class);

	public ActionStore action(String id, IAction action) {
		if (map.containsKey(id))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "action", map.get(id), action));
		map.put(id, action);
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

	@Override
	public String toString() {
		return "ActionStore [map=" + map + "]";
	}

}
