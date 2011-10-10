package org.softwareFm.display.editor;

import java.util.List;
import java.util.Map;

import org.softwareFm.display.data.ActionData;
import org.softwareFm.utilities.collections.Lists;

public class RememberUpdateStore implements IUpdateStore {

	public final List<String> entities=Lists.newList();
	public final List<String> urls=Lists.newList();
	public final List<Map<String,Object>> datas=Lists.newList();

	@Override
	public void update(ActionData actionData, String key, Object newValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void update(String entity, String url, Map<String, Object> data) {
		this.entities.add(entity);
		this.urls.add(url);
		this.datas.add(data);
	}

}
