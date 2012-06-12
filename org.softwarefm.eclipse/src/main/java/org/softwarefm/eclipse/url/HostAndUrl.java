package org.softwarefm.eclipse.url;

import org.softwarefm.utilities.strings.Strings;

public class HostAndUrl {
	public final String host;
	public final String url;

	public HostAndUrl(String host, String... fragments) {
		super();
		this.host = host;
		this.url = Strings.url(fragments);
	}

	public String getHostAndUrl() {
		return Strings.url(host, url);
	}

	@Override
	public String toString() {
		return "HostAndUrl [host=" + host + ", url=" + url + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
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
		HostAndUrl other = (HostAndUrl) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

}
