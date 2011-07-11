package org.arc4eclipse.utilities.pooling;

/** The lightweight interface for a pool. */
public interface IPoolThin<T> {

	/** returns an index for an object in the pool */
	int newObjectId();

	/** valid objects in pool */
	int size();

	T getObject(int objectId);

	/** All items in the pool can be returned to the pool, and will no longer be needed */
	void dispose();

	IObjectDefinition<T> getDefinition();

	PoolOptions getPoolOptions();

}
