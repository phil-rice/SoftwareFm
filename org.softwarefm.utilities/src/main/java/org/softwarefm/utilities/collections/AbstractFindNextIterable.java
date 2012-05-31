/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwarefm.utilities.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.softwarefm.utilities.exceptions.WrappedException;

public abstract class AbstractFindNextIterable<T, Context> implements Iterable<T> {
	abstract protected T findNext(Context context) throws Exception;

	abstract protected Context reset() throws Exception;

	@Override
	public Iterator<T> iterator() {
		try {
			return new IteratorAdaptor<T>() {
				private final Context context = reset();
				private boolean usedNext = true;
				private T next;

				@Override
				public boolean hasNext() {
					findNextItem();
					return next != null;
				}

				private void findNextItem() {
					try {
						if (usedNext) {
							usedNext = false;
							next = findNext(context);
						}
					} catch (Exception e) {
						throw WrappedException.wrap(e);
					}
				}

				@Override
				public T next() {
					findNextItem();
					if (next == null)
						throw new NoSuchElementException();
					usedNext = true;
					return next;
				}

				@Override
				public String toString() {
					return "[context=" + context + ", usedNext=" + usedNext + ", next=" + next + "]";
				}
			};
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
	}

}