package org.softwareFm.server.processors.internal;

import java.util.Set;
import java.util.UUID;

import org.softwareFm.common.collections.Sets;
import org.softwareFm.server.processors.ISaltProcessor;

public class SaltProcessor implements ISaltProcessor{

	private final Set<String> legal = Sets.newSet();
	@Override
	public String makeSalt() {
		String salt = UUID.randomUUID().toString();
		legal.add(salt);
		return salt;
	}


	@Override
	public boolean invalidateSalt(String salt) {
		return legal.remove(salt);
	}

}
