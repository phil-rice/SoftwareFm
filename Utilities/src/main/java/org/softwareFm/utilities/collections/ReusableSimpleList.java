/* This file is part of SoftwareFm
/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.utilities.collections;

import java.text.MessageFormat;
import java.util.List;

import org.softwareFm.utilities.constants.UtilityMessages;

public class ReusableSimpleList<T> implements IReusableSimpleList<T> {
	private final List<T> list = Lists.newList();
	private int index;

	@Override
	public void add(T t) {
		if (index < list.size())
			list.set(index, t);
		else
			list.add(t);
		index++;
	}

	@Override
	public int size() {
		return index;
	}

	@Override
	public T get(int i) {
		if (i < index)
			return list.get(i);
		else
			throw new IndexOutOfBoundsException(MessageFormat.format(UtilityMessages.indexOutOfBounds, i, index));
	}

	@Override
	public void clear() {
		index = 0;
	}

	@Override
	public T getAllowingOld(int i) {
		return list.get(i);
	}

	// breaks encapsulation so only use if you need the speed
	public List<T> delegateList() {
		return list;
	}

}