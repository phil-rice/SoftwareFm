package org.softwareFm.crowdsource.api.newGit.internal;

import org.softwareFm.crowdsource.api.newGit.ISingleSource;
import org.softwareFm.crowdsource.utilities.crypto.Crypto;
import org.softwareFm.crowdsource.utilities.functions.IFunction1;

public class EncryptedSingleSource implements ISingleSource {

	private final String fullRl;
	private final String crypto;
	private final IFunction1<String, String> decryptFn;

	public EncryptedSingleSource(String rl, String crypto) {
		this.fullRl = rl;
		this.crypto = crypto;
		this.decryptFn = Crypto.decryptFn(crypto);
	}

	@Override
	public String fullRl() {
		return fullRl;
	}

	@Override
	public IFunction1<String, String> decyptLine() {
		return decryptFn;
	}

	@Override
	public String encrypt(String string) {
		return Crypto.aesEncrypt(crypto, string);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((crypto == null) ? 0 : crypto.hashCode());
		result = prime * result + ((fullRl == null) ? 0 : fullRl.hashCode());
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
		EncryptedSingleSource other = (EncryptedSingleSource) obj;
		if (crypto == null) {
			if (other.crypto != null)
				return false;
		} else if (!crypto.equals(other.crypto))
			return false;
		if (fullRl == null) {
			if (other.fullRl != null)
				return false;
		} else if (!fullRl.equals(other.fullRl))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EncryptedSingleSource [fullRl=" + fullRl + ", crypto=" + crypto + "]";
	}

}
