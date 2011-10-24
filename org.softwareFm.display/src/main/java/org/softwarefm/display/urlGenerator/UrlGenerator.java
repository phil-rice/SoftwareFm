package org.softwareFm.display.urlGenerator;

import java.text.MessageFormat;
import java.util.Map;

import org.softwareFm.display.constants.DisplayConstants;
import org.softwareFm.display.data.IUrlGenerator;
import org.softwareFm.utilities.strings.Strings;

public class UrlGenerator implements IUrlGenerator {

	private final String pattern;
	private final String[] keys;

	public UrlGenerator(String pattern, String... keys) {
		this.pattern = pattern;
		this.keys = keys;
		if (keys.length == 0)
			throw new IllegalArgumentException();
	}

	@Override
	public String findUrlFor(Map<String, Object> data) {
		if (data == null || data.isEmpty())
			return null;
		Object[] values = new String[2 * keys.length + 2];
		for (int i = 0; i < keys.length; i++) {
			Object key = keys[i];
			Object value = data.get(key);
			if (value == null)
				if (data.containsKey(key))
					return null;
				else
					throw new NullPointerException(MessageFormat.format(DisplayConstants.cannotFindValueForKey, key, data));
			String cleaned = Strings.forUrl(value.toString());
			values[2 * i + 2] = cleaned;
			values[2 * i + 3] = cleaned.replace(".", "/");

		}
		values[0] = subString(values[2], 0, 2);
		values[1] = subString(values[2], 2, 4);
		return MessageFormat.format(pattern, values);
	}

	private Object subString(Object object, int i, int j) {
		String raw = Strings.nullSafeToString(object);
		if (raw.length() < j)
			return "x";
		else
			return raw.substring(i, j);
	}

}
