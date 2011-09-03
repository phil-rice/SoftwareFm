package org.softwareFm.utilities.collections;

public interface ICrud<T> {
	void add(T t);

	void delete(int index);

	T get(int index);

	void set(int index, T t);

}
