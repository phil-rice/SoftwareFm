package org.softwareFm.display.displayer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.softwareFm.display.data.DisplayConstants;
import org.softwareFm.utilities.maps.ISimpleMap;
import org.softwareFm.utilities.maps.Maps;

public class DisplayerStore implements ISimpleMap<String, IDisplayerFactory> {

	private final Map<String, IDisplayerFactory> map = Maps.newMap();

	public DisplayerStore displayer(String key, IDisplayerFactory value) {
		if (map.containsKey(key))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, "displayer", map.get(key), value));
		map.put(key, value);
		return this;
	}

	@Override
	public IDisplayerFactory get(String key) {
		return Maps.getOrException(map, key);
	}

	@Override
	public List<String> keys() {
		return new ArrayList<String>(map.keySet());
	}

	@Override
	public String toString() {
		return "DisplayerStore [map=" + map + "]";
	}

}
