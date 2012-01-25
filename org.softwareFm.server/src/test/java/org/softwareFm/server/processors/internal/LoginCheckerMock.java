package org.softwareFm.server.processors.internal;

import java.util.List;
import java.util.Map;

import org.softwareFm.server.ServerConstants;
import org.softwareFm.server.processors.ILoginChecker;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.maps.Maps;

public class LoginCheckerMock implements ILoginChecker {
	public final List<String> emails = Lists.newList();
	public final List<String> passwordHashes = Lists.newList();
	private Map<String, String> map;

	public LoginCheckerMock(String crypto, String softwareFmId) {
		setResult(crypto, softwareFmId);
	}

	@Override
	public Map<String, String> login(String email, String passwordHash) {
		emails.add(email);
		passwordHashes.add(passwordHash);
		return map;
	}

	public void setResult(String crypto, String softwareFmId) {
		this.map = Maps.makeMap(ServerConstants.softwareFmIdKey, softwareFmId, ServerConstants.cryptoKey, crypto);
	}

	public void setResultToNull() {
		this.map = null;
		
	}
}
