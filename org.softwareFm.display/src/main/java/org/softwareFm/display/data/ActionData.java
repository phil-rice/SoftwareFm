package org.softwareFm.display.data;

import java.util.List;
import java.util.Map;

public class ActionData {

	public Map<String, String> url;
	public final List<String> formalParams;
	public final List<Object> actualParams;

	public ActionData(Map<String, String> lastUrlFor, List<String> formalParams, List<Object> actualParams) {
		this.url = lastUrlFor;
		this.formalParams = formalParams;
		this.actualParams = actualParams;
	}

}
