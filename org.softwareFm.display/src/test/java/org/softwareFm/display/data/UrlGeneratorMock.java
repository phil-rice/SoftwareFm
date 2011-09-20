package org.softwareFm.display.data;


public class UrlGeneratorMock implements IUrlGenerator {

	public UrlGeneratorMock(String string) {
	}

	@Override
	public String findUrlFor(String entity, Object data) {
		return "<Url " + entity + ": " + data + ">";
	}

}
