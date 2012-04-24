package org.softwareFm.crowdsource.api.newGit.internal;

import org.softwareFm.crowdsource.api.newGit.ISingleSource;

public class SingleSource implements ISingleSource {

	private final String url;
	private final String source;
	private final String userId;
	private final String userCrypto;

	public SingleSource(String url, String source, String userId, String userCrypto) {
		this.url = url;
		this.source = source;
		this.userId = userId;
		this.userCrypto = userCrypto;
	}

	@Override
	public String toString() {
		return "SingleSource [url=" + url + ", source=" + source + ", userId=" + userId + ", userCrypto=" + userCrypto + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((userCrypto == null) ? 0 : userCrypto.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		SingleSource other = (SingleSource) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (userCrypto == null) {
			if (other.userCrypto != null)
				return false;
		} else if (!userCrypto.equals(other.userCrypto))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String url() {
		return url;
	}

	@Override
	public String source() {
		return source;
	}

	@Override
	public String userId() {
		return userId;
	}

	@Override
	public String userCrypto() {
		return userCrypto;
	}

}
