package org.softwareFm.display.data;

import java.util.List;
import java.util.Map;

import org.softwareFm.utilities.collections.Lists;

public class GuiDataListenerMock implements IGuiDataListener {

	public List<String> entities = Lists.newList();
	public List<String> urls = Lists.newList();
	public List< Map<String, Object>> contexts = Lists.newList();
	public List< Map<String, Object>> datas = Lists.newList();

	@Override
	public void data(String entity, String url, Map<String, Object> context, Map<String, Object> data) {
		this.entities.add(entity);
		this.urls.add(url);
		this.contexts.add(context);
		this.datas.add(data);
	}

}
