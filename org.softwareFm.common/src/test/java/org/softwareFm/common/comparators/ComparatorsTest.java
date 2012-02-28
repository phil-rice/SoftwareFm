/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.comparators;

import java.util.Comparator;
import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.common.maps.Maps;

public class ComparatorsTest extends TestCase {

	public void testNaturalOrder() {
		Comparator<Object> comparator = Comparators.naturalOrder();
		checkMore(comparator, 2, 1);
		checkMore(comparator, 100, 11);
		checkSame(comparator, 11, 11);

		checkMore(comparator, "2", "1");
		checkMore(comparator, "11", "100");
		checkSame(comparator, "11", "11");

		checkMore(comparator, 100, null);
		checkSame(comparator, null, null);
	}

	public void testCompareBasedOnTagInValue() {
		Map<String, Object> map = Maps.stringObjectMap(//
				"key1", Maps.stringObjectMap("tag", "2"),//
				"key2", Maps.stringObjectMap("tag", "1"),//
				"key3", Maps.stringObjectMap("tag", "3"), "noTag", Maps.stringObjectMap("a", 1), "notMap", 1);
		Comparator<String> comparator = Comparators.compareBasedOnTagInValue(map, "tag");
		checkMore(comparator, "key1", "key2");
		checkMore(comparator, "key3", "key2");
		checkMore(comparator, "key3", "not a key");
		checkMore(comparator, "key3", "noTag");
		checkMore(comparator, "key3", "notMap");

		checkSame(comparator, "key1", "key1");
		checkSame(comparator, "not a key", "also not a key");
		checkSame(comparator, "not a key", "noTag");
		checkSame(comparator, "not a key", "notMap");
	}

	private <T> void checkMore(Comparator<T> comparator, T left, T right) {
		Comparator<T> invert = Comparators.invert(comparator);

		assertTrue(comparator.compare(left, right) > 0);
		assertTrue(comparator.compare(right, left) < 0);
		assertTrue(invert.compare(left, right) < 0);
		assertTrue(invert.compare(right, left) > 0);

	}

	private <T> void checkSame(Comparator<T> comparator, T left, T right) {
		Comparator<T> invert = Comparators.invert(comparator);
		assertTrue(comparator.compare(right, left) == 0);
		assertTrue(invert.compare(right, left) == 0);
	}

}