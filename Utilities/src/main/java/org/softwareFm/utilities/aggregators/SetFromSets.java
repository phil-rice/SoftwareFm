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
