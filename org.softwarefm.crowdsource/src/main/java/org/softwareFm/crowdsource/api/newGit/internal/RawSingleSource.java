package org.softwareFm.crowdsource.api.newGit.internal;

import org.softwareFm.crowdsource.api.newGit.ISingleSource;

public class RawSingleSource implements ISingleSource {

	private final String fullRl;

	public RawSingleSource(String rl) {
		this.fullRl = rl;
	}

	@Override
	public String fullRl() {
		return fullRl; 
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fullRl == null) ? 0 : fullRl.hashCode());
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
		RawSingleSource other = (RawSingleSource) obj;
		if (fullRl == null) {
			if (other.fullRl != null)
				return false;
		} else if (!fullRl.equals(other.fullRl))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RawSingleSource [fullRl=" + fullRl + "]";
	}


	@Override
	public String encrypt(String string) {
		return string;
	}

	@Override
	public String decypt(String raw) {
		return raw;
	}

}
