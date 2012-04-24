package org.softwareFm.crowdsource.api.newGit;

import java.io.File;

public class RepoLocation {
	public final File root;
	public final String url;

	public RepoLocation(File root, String url) {
		super();
		this.root = root;
		this.url = url;
	}

	@Override
	public String toString() {
		return "RepositoryLocation [root=" + root + ", url=" + url + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((root == null) ? 0 : root.hashCode());
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
		if (root == null) {
			if (other.root != null)
				return false;
		} else if (!root.equals(other.root))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}
