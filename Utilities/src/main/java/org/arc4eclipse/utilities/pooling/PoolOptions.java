package org.arc4eclipse.utilities.pooling;

public class PoolOptions {

	public final boolean tryToBeThreadSafe;
	public final boolean cleanWhenReuse;

	public PoolOptions(boolean tryToBeThreadSafe, boolean cleanWhenReuse) {
		this.tryToBeThreadSafe = tryToBeThreadSafe;
		this.cleanWhenReuse = cleanWhenReuse;
	}

	public PoolOptions() {
		this(false, true);
	}

	public PoolOptions withCleanWhenReuse(boolean cleanWhenReuse) {
		return new PoolOptions(tryToBeThreadSafe, cleanWhenReuse);
	}

}
