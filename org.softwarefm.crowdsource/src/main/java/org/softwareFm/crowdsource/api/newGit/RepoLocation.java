package org.softwareFm.crowdsource.api.newGit;

import java.io.File;

public class RepoLocation {
	public final File dir;
	public final String url;

	public RepoLocation(File dir, String url) {
		super();
		this.dir = dir;
		this.url = url;
	}

	@Override
	public String toString() {
		return "RepoLocation [dir=" + dir + ", url=" + url + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dir == null) ? 0 : dir.hashCode());
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
		RepoLocation other = (RepoLocation) obj;
		if (dir == null) {
			if (other.dir != null)
				return false;
		} else if (!dir.equals(other.dir))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}
