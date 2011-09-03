package org.arc4eclipse.displayLists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.arc4eclipse.displayCore.api.ICodec;
import org.arc4eclipse.utilities.collections.ICrud;
import org.arc4eclipse.utilities.collections.Lists;

public class ListModel<T> implements Iterable<T>, ICrud<T> {

	private final List<T> data = Lists.newList();
	private final ICodec<T> encoder;

	private final Object lock = new Object();

	public ListModel(ICodec<T> encoder) {
		this.encoder = encoder;
	}

	public void setData(List<String> data) {
		synchronized (lock) {
			this.data.clear();
			if (data != null)
				for (String item : data) {
					T nameAndValue = encoder.fromString(item);
					if (nameAndValue != null)
						this.data.add(nameAndValue);
				}
		}
	}

	@Override
	public void set(int index, T t) {
		synchronized (lock) {
			data.set(index, t);
		}
	}

	@Override
	public void add(T t) {
		synchronized (lock) {
			data.add(t);
		}
	}

	@Override
	public void delete(int index) {
		synchronized (lock) {
			data.remove(index);
		}
	}

	public Object asDataForRepostory() {
		synchronized (lock) {
			if (data.size() == 0)
				return " ";
			String[] result = new String[data.size()];
			int i = 0;
			for (T nameAndValue : data)
				result[i++] = encoder.toString(nameAndValue);
			return result;
		}
	}

	@Override
	public Iterator<T> iterator() {
		synchronized (lock) {
			return new ArrayList<T>(data).iterator();
		}
	}

	@Override
	public T get(int index) {
		return data.get(index);
	}

}
