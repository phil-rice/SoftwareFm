package org.softwareFm.utilities.collections;

import java.text.MessageFormat;
import java.util.List;

import org.softwareFm.utilities.constants.UtilityMessages;

public class ReusableSimpleList<T> implements IReusableSimpleList<T> {
	private final List<T> list = Lists.newList();
	private int index;

	public void add(T t) {
		if (index < list.size())
			list.set(index, t);
		else
			list.add(t);
		index++;
	}

	public int size() {
		return index;
	}

	public T get(int i) {
		if (i < index)
			return list.get(i);
		else
			throw new IndexOutOfBoundsException(MessageFormat.format(UtilityMessages.indexOutOfBounds, i, index));
	}

	public void clear() {
		index = 0;
	}

	public T getAllowingOld(int i) {
		return list.get(i);
	}

	// breaks encapsulation so only use if you need the speed
	public List<T> delegateList() {
		return list;
	}

}
