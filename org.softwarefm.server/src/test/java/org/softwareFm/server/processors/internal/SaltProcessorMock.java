package org.softwareFm.server.processors.internal;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.softwareFm.server.processors.ISaltProcessor;
import org.softwareFm.utilities.collections.Lists;
import org.softwareFm.utilities.collections.Sets;

public class SaltProcessorMock implements ISaltProcessor {

	public final List<String> createdSalts = Lists.newList();
	public final Set<String> legalSalts = Sets.newSet();
	public final AtomicInteger checkAndInvalidateCount = new AtomicInteger();
	@Override
	public String makeSalt() {
		String salt = "salt " + createdSalts.size();
		createdSalts.add(salt);
		legalSalts.add(salt);
		return salt;
	}


	@Override
	public boolean invalidateSalt(String salt) {
		checkAndInvalidateCount .incrementAndGet();
		return legalSalts.remove(salt);
	}

}
