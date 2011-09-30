package org.softwareFm.display.timeline;

public class PlayItem {

	public String feedType;
	public String url;

	public PlayItem(String feedType, String url) {
		this.feedType = feedType;
		this.url = url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((feedType == null) ? 0 : feedType.hashCode());
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
		PlayItem other = (PlayItem) obj;
		if (feedType == null) {
			if (other.feedType != null)
				return false;
		} else if (!feedType.equals(other.feedType))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PlayItem [feedType=" + feedType + ", url=" + url + "]";
	}

}
