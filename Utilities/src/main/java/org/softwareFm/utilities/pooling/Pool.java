package org.softwareFm.utilities.pooling;

public class Pool<T> implements IPool<T> {
	private final IPoolThin<T> poolthin;

	public Pool(IPoolThin<T> poolThin) {
		poolthin = poolThin;
	}

	public IObjectDefinition<T> getDefinition() {
		return poolthin.getDefinition();
	}

	public void prepopulate() {

	}

	public T newObject() {
		int objectId = newObjectId();
		return getObject(objectId);
	}

	public PoolOptions getPoolOptions() {
		return poolthin.getPoolOptions();
	}

	public int size() {
		return poolthin.size();
	}

	public int newObjectId() {
		int objectId = poolthin.newObjectId();
		return objectId;
	}

	public T getObject(int objectId) {
		return poolthin.getObject(objectId);
	}

	public void dispose() {
		poolthin.dispose();
	}

}
