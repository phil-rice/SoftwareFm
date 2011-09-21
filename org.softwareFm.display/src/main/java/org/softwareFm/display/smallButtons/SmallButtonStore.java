package org.softwareFm.display.smallButtons;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.utilities.maps.ISimpleMap;
import org.softwareFm.utilities.maps.Maps;

public class SmallButtonStore implements ISimpleMap<String, ISmallButtonFactory> {

	private final Map<String, ISmallButtonFactory> map = Maps.newMap();

	public SmallButtonStore smallButton(String key, ISmallButtonFactory factory) {
		if (map.containsKey(key))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "smallButton", map.get(key), factory));
		map.put(key, factory);
		return this;
	}

	@Override
	public ISmallButtonFactory get(String key) {
		return Maps.getOrException(map, key);
	}

	@Override
	public List<String> keys() {
		return new ArrayList<String>(map.keySet());
	}

}
