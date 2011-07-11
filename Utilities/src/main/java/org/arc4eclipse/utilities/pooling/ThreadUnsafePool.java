package org.arc4eclipse.utilities.pooling;

public class ThreadUnsafePool<T> extends AbstractThinPool<T> {

	public ThreadUnsafePool(PoolOptions poolOptions, IObjectDefinition<T> defn) {
		super(poolOptions, defn);
	}

	private int next;

	public void dispose() {
		next = 0;
	}

	
	protected int makeNewObjectIndex() {
		return next++;
	}

}
