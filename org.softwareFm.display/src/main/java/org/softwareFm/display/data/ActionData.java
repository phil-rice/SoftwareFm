package org.softwareFm.display.data;

import java.util.List;
import java.util.Map;

public class ActionData {

	public Map<String, String> urlMap;
	public final List<String> formalParams;
	public final List<Object> actualParams;

	public ActionData(Map<String, String> lastUrlFor, List<String> formalParams, List<Object> actualParams) {
		this.urlMap = lastUrlFor;
		this.formalParams = formalParams;
		this.actualParams = actualParams;
	}

	@Override
	public String toString() {
		return "ActionData [url=" + urlMap + ", formalParams=" + formalParams + ", actualParams=" + actualParams + "]";
	}

}
