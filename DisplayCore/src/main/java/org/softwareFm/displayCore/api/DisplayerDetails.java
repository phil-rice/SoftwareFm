package org.softwareFm.displayCore.api;

import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.displayCore.constants.DisplayCoreConstants;

public class DisplayerDetails {
	public final String entity;
	public final String key;
	public final Map<String, String> map;

	public DisplayerDetails(String entity, Map<String, String> map) {
		this(entity, map.get(DisplayCoreConstants.key), map);
	}

	private DisplayerDetails(String entity, String key, Map<String, String> map) {
		super();
		this.entity = entity;
		this.key = key;
		this.map = map;
		if (key == null)
			throw new IllegalArgumentException(MessageFormat.format(DisplayCoreConstants.missingValueInMap, DisplayCoreConstants.key, map));
	}

	@Override
	public String toString() {
		return "DisplayerDetails [entity=" + entity + ", key=" + key + ", map=" + map + "]";
	}

	public DisplayerDetails withKey(String key) {
		return new DisplayerDetails(entity, key, map);
	}

}
