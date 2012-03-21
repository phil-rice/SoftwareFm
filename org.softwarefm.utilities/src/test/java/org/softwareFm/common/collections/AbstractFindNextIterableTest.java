/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.collections;

import java.util.Arrays;
import java.util.Iterator;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.utilities.collections.AbstractFindNextIterable;
import org.softwareFm.crowdsource.utilities.collections.Iterables;

public class AbstractFindNextIterableTest extends TestCase {

	public void testFindNextIterator() {
		checkIterable();
	}

	private void checkIterable(final Integer... ints) {
		Iterable<Integer> iterable = new AbstractFindNextIterable<Integer, Iterator<Integer>>() {

			@Override
			protected Integer findNext(Iterator<Integer> context) {
				return context.hasNext() ? context.next() : null;
			}

			@Override
			protected Iterator<Integer> reset() {
				return Arrays.asList(ints).iterator();
			}

		};
		assertEquals(Arrays.asList(ints), Iterables.list(iterable));
		assertEquals(Arrays.asList(ints), Iterables.list(iterable));
		assertEquals(Arrays.asList(ints), Iterables.list(iterable));
		assertEquals(Arrays.asList(ints), Iterables.list(iterable));
	}
}