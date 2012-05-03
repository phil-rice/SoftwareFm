package org.softwareFm.crowdsource.api.newGit;

import java.io.File;

public class RepoLocation {

	public static RepoLocation local(File root, String rl) {
		return new RepoLocation(true, new File(root, rl), rl);
	}

	public static RepoLocation remote(File localRoot, String rl) {
		return new RepoLocation(false, new File(localRoot, rl), rl);
	}

	public final boolean local;
	public final File dir;
	public final String rl;

	RepoLocation(boolean local, File dir, String rl) {
		this.local = local;
		this.dir = dir;
		this.rl = rl;
	}

	@Override
	public String toString() {
		return "RepoLocation [local=" + local + ", dir=" + dir + ", rl=" + rl + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dir == null) ? 0 : dir.hashCode());
		result = prime * result + (local ? 1231 : 1237);
		result = prime * result + ((rl == null) ? 0 : rl.hashCode());
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
		if (local != other.local)
			return false;
		if (rl == null) {
			if (other.rl != null)
				return false;
		} else if (!rl.equals(other.rl))
			return false;
		return true;
	}

}
