package org.softwareFm.crowdsource.api.newGit.facard;

public class SecurityToken {
	public final String token;
	public final String fileDigest;

	public SecurityToken(String token, String fileDigest) {
		this.token = token;
		this.fileDigest = fileDigest;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileDigest == null) ? 0 : fileDigest.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
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
		SecurityToken other = (SecurityToken) obj;
		if (fileDigest == null) {
			if (other.fileDigest != null)
				return false;
		} else if (!fileDigest.equals(other.fileDigest))
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SecurityToken [token=" + token + ", fileDigest=" + fileDigest + "]";
	}

}
