package org.arc4eclipse.arc4eclipseRepository.data.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arc4eclipse.arc4eclipseRepository.constants.Arc4EclipseRepositoryConstants;
import org.arc4eclipse.arc4eclipseRepository.data.IRepositoryDataItem;

public class AbstractRepositoryDataItem implements IRepositoryDataItem {
	private List<String> keys;
	protected final Map<String, Object> data;

	public AbstractRepositoryDataItem(Map<String, Object> data) {
		this.data = Collections.unmodifiableMap(new HashMap<String, Object>(data));

	}

	@Override
	public Object get(String key) {
		return data.get(key);
	}

	@Override
	public List<String> keys() {
		if (keys == null)
			keys = new ArrayList<String>(data.keySet());
		return keys;
	}

	@Override
	public String getString(String key) {
		Object raw = get(key);
		if (raw == null)
			return "";
		if (!(raw instanceof String))
			throw new IllegalStateException(MessageFormat.format(Arc4EclipseRepositoryConstants.valueNotAString, key, raw.getClass(), raw, data));
		return raw.toString();
	}

	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + ": " + data + "]";
	}

}
