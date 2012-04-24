package org.softwareFm.crowdsource.api.newGit.facard;

public class RepoRlAndText {
	public final String repoRl;
	public final String text;

	public RepoRlAndText(String repoRl, String text) {
		super();
		this.repoRl = repoRl;
		this.text = text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((repoRl == null) ? 0 : repoRl.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		RepoRlAndText other = (RepoRlAndText) obj;
		if (repoRl == null) {
			if (other.repoRl != null)
				return false;
		} else if (!repoRl.equals(other.repoRl))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RepoRlAndText [repoRl=" + repoRl + ", text=" + text + "]";
	}
	
}
