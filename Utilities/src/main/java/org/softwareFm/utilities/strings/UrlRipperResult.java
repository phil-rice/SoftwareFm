package org.softwareFm.utilities.strings;

public class UrlRipperResult {

	public final String url;
	public final String protocol;
	public final String resourcePath;
	public final String extension;

	public UrlRipperResult(String url, String protocol, String resourcePath, String extension) {
		super();
		this.url = url;
		this.protocol = protocol;
		this.resourcePath = resourcePath;
		this.extension = extension;
	}

}
