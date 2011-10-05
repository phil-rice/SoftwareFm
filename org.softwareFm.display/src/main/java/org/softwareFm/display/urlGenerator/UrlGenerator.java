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
		if (data == null)
			return null;
		Object[] values = new String[keys.length + 1];
		for (int i = 0; i < keys.length; i++) {
			Object key = keys[i];
			Object value = data.get(key);
			if (value == null)
				throw new NullPointerException(MessageFormat.format(DisplayConstants.cannotFindValueForKey, key, data));
			String cleaned = Strings.forUrl(value.toString());
			values[i + 1] = cleaned;

		}
		values[0] = Integer.toString(Math.abs(values[1].hashCode() % 1000));
		return MessageFormat.format(pattern, values);
	}

	public String[] cleanKeysWithHashOfFirst(String[] keys) {
		String[] result = new String[keys.length + 1];
		result[0] = Integer.toString(Math.abs(keys[0].hashCode() % 1000));
		for (int i = 0; i < result.length; i++)
			result[i + 1] = Strings.forUrl(keys[i]);
		return result;
	}

}
