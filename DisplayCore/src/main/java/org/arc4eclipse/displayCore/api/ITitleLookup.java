package org.arc4eclipse.displayCore.api;

import org.arc4eclipse.displayCore.api.impl.TitleLookup;

public interface ITitleLookup {

	String getTitle(String nameSpace, String name);

	public static class Utils {
		public static ITitleLookup titleLookup() {
			return new TitleLookup();
		}
	}

}
