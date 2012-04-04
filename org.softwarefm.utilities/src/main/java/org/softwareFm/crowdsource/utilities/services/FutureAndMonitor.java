package org.softwareFm.crowdsource.utilities.services;

import java.util.concurrent.Future;

import org.softwareFm.crowdsource.utilities.monitor.IMonitor;

public class FutureAndMonitor<T> {

	public final Future<T> future;
	public final IMonitor monitor;

	public FutureAndMonitor(Future<T> future, IMonitor monitor) {
		super();
		this.future = future;
		this.monitor = monitor;
	}

	@Override
	public String toString() {
		return "FutureAndMonitor [future=" + future + ", monitor=" + monitor + "]";
	}
}
