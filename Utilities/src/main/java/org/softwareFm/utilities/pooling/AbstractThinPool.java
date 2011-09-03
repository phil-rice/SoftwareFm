package org.softwareFm.utilities.pooling;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractThinPool<T> implements IPoolThin<T> {
	abstract protected int makeNewObjectIndex();

	private final List<T> data;
	private final PoolOptions poolOptions;
	private final IObjectDefinition<T> defn;

	public AbstractThinPool(PoolOptions poolOptions, IObjectDefinition<T> defn) {
		this.poolOptions = poolOptions;
		this.defn = defn;
		data = new ArrayList<T>();
	}

	public IObjectDefinition<T> getDefinition() {
		return defn;
	}

	public int newObjectId() {
		int objectIndex = makeNewObjectIndex();
		if (objectIndex >= data.size())
			data.add(defn.createBlank());
		else if (poolOptions.cleanWhenReuse)
			defn.clean(data.get(objectIndex));
		return objectIndex;
	}

	public PoolOptions getPoolOptions() {
		return poolOptions;
	}

	public int size() {
		return 0;
	}

	public T getObject(int objectId) {
		return data.get(objectId);
	}

}
