package org.softwareFm.crowdsource.utilities.services.internal;

import java.text.MessageFormat;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class NamedThreadFactory implements ThreadFactory {
	private final AtomicInteger count = new AtomicInteger();
	private final String pattern;

	public NamedThreadFactory(String pattern) {
		super();
		this.pattern = pattern;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r, MessageFormat.format(pattern, count.getAndIncrement()));
		thread.setDaemon(true);
		return thread;
	}
}