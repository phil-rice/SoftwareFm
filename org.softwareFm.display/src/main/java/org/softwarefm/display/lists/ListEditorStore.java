package org.softwareFm.display.lists;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.utilities.maps.ISimpleMap;
import org.softwareFm.utilities.maps.Maps;

public class ListEditorStore implements ISimpleMap<String, IListEditor> {

	private Map<String, IListEditor> map = Maps.newMap();

	@Override
	public IListEditor get(String key) {
		return Maps.getOrException(map, key);
	}

	@Override
	public List<String> keys() {
		return new ArrayList<String>(map.keySet());
	}

	public ListEditorStore register(String key, IListEditor value) {
		if (map.containsKey(key))
			throw new IllegalArgumentException(MessageFormat.format(DisplayConstants.cannotSetValueTwice, key, map.get(key), value));
		map.put(key, value);
		return this;
	}

}
