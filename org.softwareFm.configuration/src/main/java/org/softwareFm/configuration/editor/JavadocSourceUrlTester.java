package org.softwareFm.configuration.editor;

import java.net.URL;

import org.softwareFm.utilities.functions.IFunction1;

public class JavadocSourceUrlTester implements IFunction1<String, String>{

	@Override
	public String apply(String from)  {
		try {
			new URL(from);
		} catch (Exception e) {
			return "Not a legal Url";
		}
		return null;
	}

}
