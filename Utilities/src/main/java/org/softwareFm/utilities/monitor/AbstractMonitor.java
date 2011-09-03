package org.softwareFm.utilities.monitor;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractMonitor implements IMonitor {

	private final AtomicBoolean cancel = new AtomicBoolean();
	private final AtomicBoolean finished = new AtomicBoolean();

	public void cancel() {
		cancel.set(true);
	}

	public boolean cancelled() {
		return cancel.get();
	}

	public void finish() {
		finished.set(true);
	}

	public boolean done() {
		return finished.get();
	}

}
