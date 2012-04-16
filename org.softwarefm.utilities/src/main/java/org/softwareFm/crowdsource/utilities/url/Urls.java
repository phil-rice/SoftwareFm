/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.crowdsource.utilities.url;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.exceptions.WrappedException;
import org.softwareFm.crowdsource.utilities.strings.Strings;
import org.softwareFm.crowdsource.utilities.strings.UrlRipperResult;

public class Urls {

	public static List<String> urlsToRoot(String url) {
		List<String> segments = Strings.splitIgnoreBlanks(url, "/");
		List<String> result = Lists.newList();
		for (int i = 0; i<segments.size(); i++)
		 result.add(composeFirst(i+1, segments));
		return result;
	}

	public static String removeSlash(String raw) {
		if (raw.startsWith("/"))
			return raw.substring(1);
		else
			return raw;
	}

	public static String compose(String... urls) {
		return composeFirst(urls.length, urls);
	}

	public static String composeFirst(int n, String... urls) {
		return composeFirst(n, Arrays.asList(urls));
	}

	public static String composeFirst(int n, List<String> urls) {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for (String url : urls) {
			if (i++ >= n)
				break;
			if (builder.length() > 0) {
				if (builder.charAt(builder.length() - 1) != '/')
					builder.append('/');
			}
			if (url.startsWith("/"))
				builder.append(url.substring(1));
			else
				builder.append(url);
		}
		return builder.toString();
	}

	public static String composeWithSlash(String... urls) {
		return "/" + compose(urls);
	}

	public static UrlRipperResult rip(String url) {
		if (url == null || url.equals("")) {
			return new UrlRipperResult(url, "", "", "");
		}
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

	public static URI withDefaultProtocol(String protocol, String text) {
		try {
			UrlRipperResult rip = rip(text);
			if (rip.protocol.equals(""))
				return new URI(protocol + "://" + rip.url);
			else
				return new URI(rip.url);
		} catch (URISyntaxException e) {
			throw WrappedException.wrap(e);
		}
	}

	@SuppressWarnings("unused")
	public static boolean isUrl(String string) {
		try {
			new URL(string);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
}