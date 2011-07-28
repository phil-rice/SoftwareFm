package org.arc4eclipse.displayCore.api;

import java.util.Comparator;

import org.arc4eclipse.displayCore.api.impl.TitleLookup;

public interface ITitleLookup extends Comparator<NameSpaceAndName> {

	String getTitle(String nameSpace, String name);

	public static class Utils {
		public static ITitleLookup titleLookup() {
			return new TitleLookup();
		}
	}

}
