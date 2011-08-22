package org.arc4eclipse.displayCore.api;

import java.util.Map;

import org.arc4eclipse.displayCore.constants.DisplayCoreConstants;

public class DisplayerDetails {
	public final String entity;
	public final String key;
	public final Map<String, String> map;

	public DisplayerDetails(String entity, Map<String, String> map) {
		super();
		this.entity = entity;
		this.map = map;
		this.key = map.get(DisplayCoreConstants.key);
	}

	@Override
	public String toString() {
		return "DisplayerDetails [entity=" + entity + ", key=" + key + ", map=" + map + "]";
	}

}
