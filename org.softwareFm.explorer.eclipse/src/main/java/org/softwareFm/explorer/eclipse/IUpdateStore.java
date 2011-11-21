package org.softwareFm.explorer.eclipse;

import java.util.Map;

import org.softwareFm.display.data.ActionData;

public interface IUpdateStore {
	void update(ActionData actionData, String key, final Object newValue);

	void update(final String entity, final String url, final Map<String, Object> data);
}
