package org.softwareFm.utilities.strings;

public class PreAndPost {
	public String pre;
	public String post;

	public PreAndPost(String pre, String post) {
		this.pre = pre;
		this.post = post;
	}

	@Override
	public String toString() {
		return "PreAndPost [pre=" + pre + ", post=" + post + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((post == null) ? 0 : post.hashCode());
		result = prime * result + ((pre == null) ? 0 : pre.hashCode());
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
		PreAndPost other = (PreAndPost) obj;
		if (post == null) {
			if (other.post != null)
				return false;
		} else if (!post.equals(other.post))
			return false;
		if (pre == null) {
			if (other.pre != null)
				return false;
		} else if (!pre.equals(other.pre))
			return false;
		return true;
	}
}
