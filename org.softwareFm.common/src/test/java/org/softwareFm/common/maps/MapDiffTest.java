package org.softwareFm.common.maps;

import java.util.Map;

import junit.framework.TestCase;

import org.softwareFm.common.collections.Lists;
import org.softwareFm.common.maps.MapDiff;
import org.softwareFm.common.maps.Maps;

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
