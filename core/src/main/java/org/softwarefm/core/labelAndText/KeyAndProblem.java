package org.softwarefm.core.labelAndText;

public class KeyAndProblem {
	public final String key;
	public final String problem;

	@Override
	public String toString() {
		return "KeyAndProblem [key=" + key + ", problem=" + problem + "]";
	}

	public KeyAndProblem(String key, String problem) {
		super();
		this.key = key;
		this.problem = problem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((problem == null) ? 0 : problem.hashCode());
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
		KeyAndProblem other = (KeyAndProblem) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (problem == null) {
			if (other.problem != null)
				return false;
		} else if (!problem.equals(other.problem))
			return false;
		return true;
	}
}
