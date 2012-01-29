package org.softwareFm.server.processors.internal;

import java.util.List;

import org.softwareFm.server.processors.IPasswordChanger;
import org.softwareFm.utilities.collections.Lists;

public class PasswordChangerMock implements IPasswordChanger {

	public final List<String> emails = Lists.newList();
	public final List<String> oldHashs = Lists.newList();
	public final List<String> newHashs = Lists.newList();
	private boolean ok;

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	@Override
	public boolean changePassword(String email, String oldHash, String newHash) {
		emails.add(email);
		oldHashs.add(oldHash);
		newHashs.add(newHash);
		return ok;
	}

}
