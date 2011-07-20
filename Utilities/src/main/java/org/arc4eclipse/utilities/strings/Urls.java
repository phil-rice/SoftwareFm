package org.arc4eclipse.utilities.strings;

public class Urls {

	public static UrlRipperResult rip(String url) {
		int firstIndexOfSlashSlash = url.indexOf("://");
		boolean hasProtocol = firstIndexOfSlashSlash >= 0;
		int protocolEnds = hasProtocol ? firstIndexOfSlashSlash : 0;
		int afterProtocol = hasProtocol ? firstIndexOfSlashSlash + 3 : 0;
		String protocol = url.substring(0, protocolEnds);
		String withoutProtocol = url.substring(afterProtocol);
		int lastDotIndex = withoutProtocol.lastIndexOf('.');
		int lastSlashIndex = withoutProtocol.lastIndexOf('/');
		boolean hasExtension = lastDotIndex > lastSlashIndex;
		String resourcePath = hasExtension ? withoutProtocol.substring(0, lastDotIndex) : withoutProtocol;
		String extension = hasExtension ? withoutProtocol.substring(lastDotIndex + 1) : "";
		return new UrlRipperResult(url, protocol, resourcePath, extension);

	}
}
