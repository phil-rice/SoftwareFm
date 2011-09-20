package org.softwareFm.display.data;

import java.util.Map;

public interface IGuiDataListener {

	void data(String entity, String url, Map<String, Object> context, Map<String, Object> data);

}
