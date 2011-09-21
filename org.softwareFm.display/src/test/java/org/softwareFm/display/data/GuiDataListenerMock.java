package org.softwareFm.display.data;

import java.util.List;

import org.softwareFm.utilities.collections.Lists;

public class GuiDataListenerMock implements IGuiDataListener {

	public List<String> entities = Lists.newList();
	public List<String> urls = Lists.newList();

	@Override
	public void data(String entity, String url) {
		this.entities.add(entity);
		this.urls.add(url);
	}

}
