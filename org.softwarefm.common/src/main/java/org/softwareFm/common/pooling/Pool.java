/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.pooling;

public class Pool<T> implements IPool<T> {
	private final IPoolThin<T> poolthin;

	public Pool(IPoolThin<T> poolThin) {
		poolthin = poolThin;
	}

	@Override
	public IObjectDefinition<T> getDefinition() {
		return poolthin.getDefinition();
	}

	@Override
	public void prepopulate() {

	}

	@Override
	public T newObject() {
		int objectId = newObjectId();
		return getObject(objectId);
	}

	@Override
	public PoolOptions getPoolOptions() {
		return poolthin.getPoolOptions();
	}

	@Override
	public int size() {
		return poolthin.size();
	}

	@Override
	public int newObjectId() {
		int objectId = poolthin.newObjectId();
		return objectId;
	}

	@Override
	public T getObject(int objectId) {
		return poolthin.getObject(objectId);
	}

	@Override
	public void dispose() {
		poolthin.dispose();
	}

}