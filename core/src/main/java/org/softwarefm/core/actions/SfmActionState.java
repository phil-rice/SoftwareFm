package org.softwarefm.core.actions;

public class SfmActionState {
	private String urlSuffix;

	public void setUrlSuffix(String urlSuffix) {
		this.urlSuffix = urlSuffix;
	}

	public String getUrlSuffix() {
		return urlSuffix;
	}

	@Override
	public String toString() {
		return "SfmActionState [urlSuffix=" + urlSuffix + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((urlSuffix == null) ? 0 : urlSuffix.hashCode());
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
		SfmActionState other = (SfmActionState) obj;
		if (urlSuffix == null) {
			if (other.urlSuffix != null)
				return false;
		} else if (!urlSuffix.equals(other.urlSuffix))
			return false;
		return true;
	}

}
