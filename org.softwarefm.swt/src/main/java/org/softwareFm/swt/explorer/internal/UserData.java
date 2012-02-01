package org.softwareFm.swt.explorer.internal;

import org.softwareFm.common.strings.Strings;

public class UserData {

	public static UserData blank() {
		return new UserData(null, null, null);
	}

	public final String email;
	public final String softwareFmId;
	public final String crypto;

	public UserData(String email, String softwareFmId, String crypto) {
		this.email = email;
		this.softwareFmId = softwareFmId;
		this.crypto = crypto;
	}

	public boolean isLoggedIn() {
		return crypto != null;
	}

	public String email() {
		return Strings.nullSafeToString(email);
	}

	public UserData loggedOut() {
		return new UserData(email, null, null);
	}

	@Override
	public String toString() {
		return "UserData [email=" + email + ", softwareFmId=" + softwareFmId + ", crypto=" + crypto + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((crypto == null) ? 0 : crypto.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((softwareFmId == null) ? 0 : softwareFmId.hashCode());
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
		UserData other = (UserData) obj;
		if (crypto == null) {
			if (other.crypto != null)
				return false;
		} else if (!crypto.equals(other.crypto))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (softwareFmId == null) {
			if (other.softwareFmId != null)
				return false;
		} else if (!softwareFmId.equals(other.softwareFmId))
			return false;
		return true;
	}

}
