/* SoftwareFm is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.*/
/* SoftwareFm is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. */
/* You should have received a copy of the GNU General Public License along with SoftwareFm. If not, see <http://www.gnu.org/licenses/> */

package org.softwareFm.common.maps;

import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.crowdsource.utilities.collections.Lists;
import org.softwareFm.crowdsource.utilities.maps.MapDiff;
import org.softwareFm.crowdsource.utilities.maps.Maps;

public class MapDiffTest extends TestCase {

	public void test() {
		Map<Object, Object> a1b2 = Maps.makeLinkedMap("a", 1, "b", 2);
		Map<Object, Object> b2c3 = Maps.makeLinkedMap("b", 2, "c", 3);
		MapDiff<Object> diffForTests = Maps.diffForTests(a1b2, b2c3);
		assertEquals("MapDiff\n" + //
				" keysIn1Not2=[a]\n" + //
				" keysIn2Not1=[c]\n" + //
				" valuesDifferent=\n" + //
				"   a-> [1]  and  [null]\n", diffForTests.toString());
		assertEquals("a", Lists.getOnly(diffForTests.keysIn1Not2));
		assertEquals("c", Lists.getOnly(diffForTests.keysIn2Not1));
	}
}