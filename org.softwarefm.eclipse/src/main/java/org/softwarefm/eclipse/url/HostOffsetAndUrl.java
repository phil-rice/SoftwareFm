package org.softwarefm.eclipse.url;

import java.net.MalformedURLException;
import java.net.URL;

import org.softwarefm.utilities.exceptions.WrappedException;
import org.softwarefm.utilities.strings.Strings;

public class HostOffsetAndUrl {
	public final String host;
	public final String offset;
	public final String url;

	public HostOffsetAndUrl(String host, String offset, String... fragments) {
		super();
		this.host = host;
		this.offset = offset;
		this.url = Strings.url(fragments);
	}

	public String getHostAndUrl() {
		return Strings.url(host, offset, url);
	}
	public String getHttpHostAndUrl() {
		return "http://" + Strings.url(host, offset, url);
	}

	@Override
	public String toString() {
		return "HostOffsetAndUrl [host=" + host + ", offset=" + offset + ", url=" + url + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((offset == null) ? 0 : offset.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HostOffsetAndUrl other = (HostOffsetAndUrl) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (offset == null) {
			if (other.offset != null)
				return false;
		} else if (!offset.equals(other.offset))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	public String getOffsetAndUrl() {
		return Strings.url(offset, url);
	}

	public URL getHttpUrl() {
		try {
			return new URL(getHttpHostAndUrl());
		} catch (MalformedURLException e) {
			throw WrappedException.wrap(e);
		}
	}

}
