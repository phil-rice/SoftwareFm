/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.pooling;

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

	@Override
	public IObjectDefinition<T> getDefinition() {
		return defn;
	}

	@Override
	public int newObjectId() {
		int objectIndex = makeNewObjectIndex();
		if (objectIndex >= data.size())
			data.add(defn.createBlank());
		else if (poolOptions.cleanWhenReuse)
			defn.clean(data.get(objectIndex));
		return objectIndex;
	}

	@Override
	public PoolOptions getPoolOptions() {
		return poolOptions;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public T getObject(int objectId) {
		return data.get(objectId);
	}

}