package org.softwarefm.eclipse.usage;

public class UsageStats {

	public final int count;

	public UsageStats(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "UsageStats [count=" + count + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + count;
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
		UsageStats other = (UsageStats) obj;
		if (count != other.count)
			return false;
		return true;
	}

}
