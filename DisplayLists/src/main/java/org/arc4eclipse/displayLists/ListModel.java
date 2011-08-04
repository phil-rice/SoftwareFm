package org.arc4eclipse.displayLists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.arc4eclipse.utilities.collections.Lists;

public class ListModel implements Iterable<NameAndUrl> {

	private final List<NameAndUrl> data = Lists.newList();
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
					NameAndUrl nameAndUrl = encoder.fromString(item);
					if (nameAndUrl != null)
						this.data.add(nameAndUrl);
				}
		}
	}

	public void add(String name, String url) {
		synchronized (lock) {
			data.add(new NameAndUrl(name, url));
		}
	}

	public void set(int index, String name, String url) {
		synchronized (lock) {
			data.set(index, new NameAndUrl(name, url));
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
			for (NameAndUrl nameAndUrl : data)
				result[i++] = encoder.toString(nameAndUrl);
			return result;
		}
	}

	@Override
	public Iterator<NameAndUrl> iterator() {
		synchronized (lock) {
			return new ArrayList<NameAndUrl>(data).iterator();
		}
	}

	public NameAndUrl get(int index) {
		return data.get(index);
	}
}
