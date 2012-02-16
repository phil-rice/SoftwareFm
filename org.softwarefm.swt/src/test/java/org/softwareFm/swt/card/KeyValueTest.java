/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.swt.card;

import java.util.Comparator;

import junit.framework.TestCase;

import org.softwareFm.common.functions.IFunction1;
import org.softwareFm.common.tests.Tests;

public class KeyValueTest extends TestCase {

	public void testConstructor() {
		LineItem lineItem = new LineItem(null, "key", "value");
		assertEquals("key", lineItem.key);
		assertEquals("value", lineItem.value);
	}

	public void testKeyFunctio() throws Exception {
		assertEquals("key", LineItem.Utils.keyFn().apply(new LineItem(null, "key", "value")));

	}

	public void testEquality() {
		Tests.checkEqualityAndHashcode(new IFunction1<String, LineItem>() {
			@Override
			public LineItem apply(String from) throws Exception {
				return new LineItem(null, from + "_key", from + "_value");
			}
		});
		assertFalse(new LineItem(null, "k", "v").equals(new LineItem(null, "k", "v2")));
		assertFalse(new LineItem(null, "k", "v").equals(new LineItem(null, "k2", "v")));
	}

	public void testForItemsInSortOrder() {
		checkComparator("d", "c", -1);
		checkComparator("d", "b", -2);
		checkComparator("c", "b", -1);
		checkComparator("b", "b", -0);
	}

	public void testAllItemsInSortOrderAreLessThanAnyOther() {
		checkComparator("b", "a", -1);
		checkComparator("c", "a", -1);
		checkComparator("d", "a", -1);

		checkComparator("b", "e", -1);
		checkComparator("c", "e", -1);
		checkComparator("d", "e", -1);
	}

	public void testSortedAlphabeticallyIfNotInSortOrder() {
		checkComparator("a", "a", 0);
		checkComparator("a", "f", -5);
		checkComparator("e", "f", -1);
		checkComparator("a", "e", -4);
	}

	private void checkComparator(String left, String right, int expected) {
		Comparator<LineItem> comparator = LineItem.Utils.orderedKeyComparator("d", "c", "b");
		assertEquals(expected, comparator.compare(new LineItem(null, left, null), new LineItem(null, right, null)));
		assertEquals(-expected, comparator.compare(new LineItem(null, right, null), new LineItem(null, left, null)));
	}

}