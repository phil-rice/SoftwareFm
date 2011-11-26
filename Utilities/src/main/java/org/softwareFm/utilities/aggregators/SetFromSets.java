/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.aggregators;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.softwareFm.utilities.collections.Iterables;

public class SetFromSets<T> implements Set<T> {

	private final Iterable<Set<T>> sets;

	public SetFromSets(Iterable<Set<T>> sets) {
		this.sets = sets;
	}

	public int size() {
		int size = 0;
		for (Set<T> set : sets)
			size += set.size();
		return size;
	}

	public boolean isEmpty() {
		for (Set<T> set : sets)
			if (!set.isEmpty())
				return false;
		return true;
	}

	public boolean contains(Object key) {
		for (Set<T> set : sets)
			if (set.contains(key))
				return true;
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		for (Object x : c)
			if (!contains(x))
				return false;
		return true;
	}

	public Iterator<T> iterator() {
		return Iterables.split(sets).iterator();
	}

	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	public <E> E[] toArray(E[] a) {
		throw new UnsupportedOperationException();
	}

	public boolean add(T o) {
		throw new UnsupportedOperationException();
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

}