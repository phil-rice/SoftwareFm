package org.arc4eclipse.displayLists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.arc4eclipse.utilities.collections.Lists;

public class ListModel implements Iterable<NameAndValue> {

	private final List<NameAndValue> data = Lists.newList();
	private final IEncodeDecodeNameAndUrl encoder;

	private final Object lock = new Object();

	public ListModel(IEncodeDecodeNameAndUrl encoder) {
		this.encoder = encoder;
	}

	public void setData(List<String> data) {
		synchronized (lock) {
			this.data.clear();
			if (data != null)
				for (String item : data) {
					NameAndValue nameAndValue = encoder.fromString(item);
					if (nameAndValue != null)
						this.data.add(nameAndValue);
				}
		}
	}

	public void add(String name, String url) {
		synchronized (lock) {
			data.add(new NameAndValue(name, url));
		}
	}

	public void set(int index, String name, String url) {
		synchronized (lock) {
			data.set(index, new NameAndValue(name, url));
		}

	}

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
			for (NameAndValue nameAndValue : data)
				result[i++] = encoder.toString(nameAndValue);
			return result;
		}
	}

	@Override
	public Iterator<NameAndValue> iterator() {
		synchronized (lock) {
			return new ArrayList<NameAndValue>(data).iterator();
		}
	}

	public NameAndValue get(int index) {
		return data.get(index);
	}
}
